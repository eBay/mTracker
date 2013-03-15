package com.ebay.build.profiler.profile;

import java.net.UnknownHostException;
import java.util.Date;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private CalTransaction discoveryTransaction;
	private ExecutionEvent event;
	
	public DiscoveryProfile() {
		super(new Timer());
		
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction("DEV", "Environment", "data");
		}
	}

	public DiscoveryProfile(Context context, ExecutionEvent event) {
		super(new Timer(), event, context);
		
		String transName= getTransactionName(event);
		context.getData().put("build.env", transName);
	
		if(calogger.isCalInitialized()) {
			String data = populateData(event);
			
			if (this.isInJekins()) {
				getSession().setEnvironment(transName);
				getSession().setPayload(data);
				getSession().setStartTime(new Date(this.getTimer().getStartTime()));
			} else {
				discoveryTransaction = calogger.startCALTransaction(transName, "Environment",  data);
			}
		}
		System.out.println("[INFO] CAL logging Enabled: " + this.isCALEnabled());
		System.out.println("[INFO] Running From CI: " + this.isInJekins());
		System.out.println("[INFO] Build Environment: " + context.getData().get("build.env"));
	}
	
	private String populateData(ExecutionEvent event) {
		StringBuilder data = new StringBuilder();
		data.append("git.url=").append(this.getGitRepoUrl());
		
		if(System.getenv("BUILD_URL") != null) {
            data.append(";jenkins.url=").append(System.getenv("BUILD_URL"));
            data.append(";git.branch=").append(System.getenv("GIT_BRANCH"));
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

	@Override
	public void stop() {
		
		super.stop();
		
		if(calogger.isCalInitialized()) {
			String status = endTransaction(discoveryTransaction);
			
			if (this.isInJekins()) {
				this.getSession().setDuration(this.getElapsedTime());
				this.getSession().setStatus(status);
			} else {
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
}
