package com.ebay.build.profiler.profile;

import java.net.UnknownHostException;
import java.util.Date;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.cal.processors.ProcessHelper;
import com.ebay.build.cal.processors.SessionExporter;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private CalTransaction discoveryTransaction;
	
	public DiscoveryProfile() {
		super(new Timer());
		
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction("DEV", "Environment", "data");
		}
	}

	public DiscoveryProfile(Context context, ExecutionEvent event) {
		super(new Timer(), event, context);
		
		String transName= getBuildEnvironment();
		
		String data = populateData();
		
		if (this.isInJekins()) {
			getSession().setEnvironment(transName);
			getSession().setStartTime(new Date(this.getTimer().getStartTime()));
			ProcessHelper.parseSessionPayLoad(data, getSession());
		}
	
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction(transName, "Environment",  data);
		}
		
		System.out.println("[INFO] CAL logging Enabled: " + this.isCALEnabled());
		System.out.println("[INFO] Running From CI: " + this.isInJekins());
		System.out.println("[INFO] Build Environment: " + this.getBuildEnvironment());
	}
	
	private String populateData() {
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
			String mavenVersion = System.getProperty("maven.home");
			String javaVersion = System.getProperty("java.runtime.version");
			data.append(";maven.version=").append(mavenVersion).append(";java.version=").append(javaVersion);
		}	
		
		return data.toString();
	}

	@Override
	public void stop() {
		
		super.stop();
		
		String status = endTransaction(discoveryTransaction);
		
		if (this.isInJekins()) {
			this.getSession().setDuration(this.getElapsedTime());
			this.getSession().setStatus(status);
			
			exportSession();
			return;
		}
		
		if (isCalInitialized()) {
			try {
				System.out.println("Stopping CAL Service...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			calogger.destroy();
		}
	}
	
//	private void importToDB() {
//		try {
//			LoaderProcessor processor = new LoaderProcessor(event.getSession().getSystemProperties().getProperty("maven.home"));
//			System.out.println("[INFO] Storing Session into DB...");
//			processor.process(getSession());
//			System.out.println("[INFO] Session store completed!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private void exportSession() {
		SessionExporter exporter = new SessionExporter();
		exporter.process(this.getSession());
	}
}
