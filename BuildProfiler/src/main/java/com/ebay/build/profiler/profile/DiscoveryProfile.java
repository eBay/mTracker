package com.ebay.build.profiler.profile;

import java.net.UnknownHostException;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private CalTransaction discoveryTransaction;
	private ExecutionEvent event;
	
	private String gitRepoUrl;
	private boolean isCalEnabled;
	
	public DiscoveryProfile() {
		super(new Timer());
		
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction("DEV" , "data");
		}
	}

	public DiscoveryProfile(Context context, ExecutionEvent event) {
		super(new Timer(), event, context);
	
		if(isCalEnabled) {
			String transName= getTransactionName();
			String data = populateData();
			discoveryTransaction = calogger.startCALTransaction(transName , data);
		}
	}
	
	private String populateData() {
		StringBuilder data = new StringBuilder();
		data.append("Git URL: ").append(gitRepoUrl);
		
		if(System.getenv("BUILD_URL") != null) {
            data.append(";jenkins_url=").append(System.getenv("BUILD_URL"));
            data.append(";git_branch").append(System.getenv("GIT_BRANCH"));
        }
		
		try {
			data.append(";machine=").append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {}
		
		data.append(";username=").append(System.getProperty("user.name"));
		
		return data.toString();
	}

	private String getTransactionName() {
		String transName="";
		
		if(System.getenv("BUILD_URL") != null) {
			transName = "CI";
		} else {
			transName = "DEV";
		}
		
		return transName;
	}

	@Override
	public void stop() {
		
		super.stop();
		
		if(isCalEnabled) {
			if(event.getSession().getResult().getExceptions().size() > 0) {
				calogger.endCALTransaction(discoveryTransaction,"FAILED", event.getException());
			} else {
				calogger.endCALTransaction(discoveryTransaction, "0");
			}
		
			try {
				System.out.println("Stopping CAL Service...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			calogger.destroy();
		}
		
	}

}
