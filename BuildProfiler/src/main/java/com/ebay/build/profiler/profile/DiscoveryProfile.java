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
	
	
	public DiscoveryProfile() {
		super(new Timer());
		
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction("DEV" , "data");
		}
	}

	public DiscoveryProfile(Context context, ExecutionEvent event) {
		super(new Timer(), event, context);
	
		if(calogger.isCalInitialized()) {
			String transName= getTransactionName(event);
			String data = populateData(event);
			discoveryTransaction = calogger.startCALTransaction(transName , data);
		}
	}
	
	private String populateData(ExecutionEvent event) {
		StringBuilder data = new StringBuilder();
		data.append("git_url=").append(gitRepoUrl);
		
		if(System.getenv("BUILD_URL") != null) {
            data.append(";jenkins_url=").append(System.getenv("BUILD_URL"));
            data.append(";git_branch").append(System.getenv("GIT_BRANCH"));
        }
		
		try {
			data.append(";machine=").append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {}
		
		data.append(";uname=").append(System.getProperty("user.name"));
		
		if (event != null) {
			String mavenVersion = event.getSession().getSystemProperties().getProperty("maven.build.version");
			String javaVersion = System.getProperty("java.runtime.version");
			data.append(";maven.version=").append(mavenVersion).append(";java.version=").append(javaVersion);
		}	
		
		return data.toString();
	}

	private String getTransactionName(ExecutionEvent event) {
		String transName = event.getSession().getSystemProperties().getProperty("build.env");
		if (null != transName) {
			return transName;
		}
		
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
		
		if(calogger.isCalInitialized()) {
			if(event != null && event.getSession().getResult().getExceptions().size() > 0) {
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
