package com.ebay.build.profiler.profile;

import java.io.File;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.CALLogger;
import com.ebay.build.profiler.util.GitUtil;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private CalTransaction discoveryTransaction;
	private ExecutionEvent event;
	
	private String gitRepoUrl;
	private boolean isCalEnabled;
	
	public DiscoveryProfile() {
		super(new Timer());
		
		URL calConfig = getClass().getClassLoader().getResource("cal.properties");
		boolean isCalEnabled = CALLogger.initialize(calConfig, "RaptorBuild");
		
		if(isCalEnabled) {
			discoveryTransaction = CALLogger.startCALTransaction("DEV" , "data");
		}
	}

	public DiscoveryProfile(ExecutionEvent event) {
		super(new Timer());
		this.event = event;
		
		String poolName = getAppName();
		
		URL calConfig = getClass().getClassLoader().getResource("cal.properties");
		isCalEnabled = CALLogger.initialize(calConfig, poolName);
		
		if(isCalEnabled) {
			String transName= getTransactionName();
			String data = populateData();
			discoveryTransaction = CALLogger.startCALTransaction(transName , data);
		}
	}
	
	private String getAppName() {
		File gitMeta = GitUtil.findGitRepository(new File(event.getSession().getExecutionRootDirectory()));
		String gitURL = "";
		
		if(gitMeta != null && gitMeta.exists()) {
			File gitConfig = new File(new File(gitMeta,".git"), "config");
			gitURL =  GitUtil.getRepoName(gitConfig);
			if(gitURL != null) {
				this.gitRepoUrl = gitURL;
			}
		}
				
		String appName = getCALPoolName(gitURL);
		
		return appName;
	}
	
	private String getCALPoolName(String gitURL) {
		String appName = "UNKNOWN";
		
		if ( gitURL.startsWith("https:") || gitURL.startsWith("git@") || gitURL.startsWith("git:") ) {
			appName = gitURL.substring(gitURL.lastIndexOf("/")+1);
			if(appName.endsWith(".git")) {
				appName = appName.substring(0, appName.lastIndexOf("."));
			}
		}
		
		System.out.println("Cal Pool Name : " + appName);
		return appName;
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
				CALLogger.endCALTransaction(discoveryTransaction,"FAILED", event.getException());
			} else {
				CALLogger.endCALTransaction(discoveryTransaction, "0");
			}
		
			try {
				System.out.println("Stopping CAL Service...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CALLogger.destroy();
		}
		
		
		
	}

}
