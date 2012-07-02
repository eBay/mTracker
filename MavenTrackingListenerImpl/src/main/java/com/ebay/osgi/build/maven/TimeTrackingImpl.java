package com.ebay.osgi.build.maven;

import java.io.File;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.kernel.calwrapper.CalTransaction;
import com.ebay.osgi.build.util.CALLoggerUtil;
import com.ebay.osgi.build.util.GitUtil;

public class TimeTrackingImpl extends AbstractExecutionListener {
	
	private CalTransaction discoveryTransaction;
	private CalTransaction sessionTransaction;
	private CalTransaction projectTransaction;
	private CalTransaction mojoTransaction;
	
	private boolean isCalEnabled;
	
	
	public TimeTrackingImpl() {
		
	}

	@Override
	public void projectDiscoveryStarted(ExecutionEvent event) {
		
		StringBuilder data = new StringBuilder();
		data.append("goals=").append(event.getSession().getGoals().toString());
		data.append(";options=").append(event.getSession().getUserProperties().toString());

		File gitMeta = GitUtil.findGitRepository(new File(event.getSession().getExecutionRootDirectory()));
		String gitURL = "";
		if(gitMeta != null && gitMeta.exists()) {
			File gitConfig = new File(new File(gitMeta,".git"), "config");
			gitURL =  GitUtil.getRepoName(gitConfig);
			if(gitURL != null) {
				data.append(";git_repo=").append(gitURL);
			}
		}
				
		URL calConfig = getClass().getClassLoader().getResource("cal.properties");
		String appName = getCALPoolName(gitURL);
		
		
		isCalEnabled = CALLoggerUtil.initialize(calConfig, appName);
		System.out.println("CAL Enabled : " + isCalEnabled);

		if(System.getenv("BUILD_URL") != null) {
             data.append(";jenkins_url=").append(System.getenv("BUILD_URL"));
             data.append(";git_branch").append(System.getenv("GIT_BRANCH"));
         }
		
		
		try {
			data.append(";machine=").append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {}
		
		data.append(";username=").append(System.getProperty("user.name"));
		
		String transName="";
		if(System.getenv("BUILD_URL") != null) {
			transName = "CI";
		} else {
			transName = "DEV";
		}
		
		if(isCalEnabled) {
			discoveryTransaction = CALLoggerUtil.startCALTransaction(transName , data.toString());
		}
	}
	
	
	public String getCALPoolName(String gitURL) {
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


	@Override
	public void sessionStarted(ExecutionEvent event) {
		if(isCalEnabled) {
			sessionTransaction = CALLoggerUtil.startCALTransaction(event.getSession().getTopLevelProject().getId(), "SESSION", "");
		}
	}
	
	/*public static void main(String[] args) {
		TimeTrackingImpl m = new TimeTrackingImpl();
		System.out.println("1: " + m.getCALPoolName("https://github.scm.corp.ebay.com/sonsinha/RaptorMetadataPlugin.git"));
		System.out.println("2: " + m.getCALPoolName("https://github.scm.corp.ebay.com/sonsinha/RaptorMetadataPlugin"));
		System.out.println("3: " + m.getCALPoolName("git@github.scm.corp.ebay.com:sonsinha/RaptorMetadataPlugin.git"));
		System.out.println("4: " + m.getCALPoolName("git@github.scm.corp.ebay.com:sonsinha/RaptorMetadataPlugin"));
		System.out.println("5: " + m.getCALPoolName("git://github.scm.corp.ebay.com/sonsinha/RaptorMetadataPlugin.git"));
		System.out.println("6: " + m.getCALPoolName("git://github.scm.corp.ebay.com/sonsinha/RaptorMetadataPlugin"));
	}*/

	@Override
	public void sessionEnded(ExecutionEvent event) {
		if(isCalEnabled) {
			if(event.getSession().getResult().getExceptions().size() > 0) {
				CALLoggerUtil.endCALTransaction(sessionTransaction, "FAILED", event.getSession().getResult().getExceptions().get(0));
				CALLoggerUtil.endCALTransaction(discoveryTransaction, "FAILED", event.getException());
			}
			else {
				CALLoggerUtil.endCALTransaction(sessionTransaction, "0");
				CALLoggerUtil.endCALTransaction(discoveryTransaction, "0");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			CALLoggerUtil.destroy();
		}
	}

	@Override
	public void projectSkipped(ExecutionEvent event) {
		if(isCalEnabled) {
			CALLoggerUtil.logCALEvent("SKIPPED", event.getProject().getId());
		}
	}

	@Override
	public void projectStarted(ExecutionEvent event) {
		if(isCalEnabled) {
			projectTransaction = CALLoggerUtil.startCALTransaction(event.getProject().getId(),"PROJECT", "");
		}
	}

	@Override
	public void projectSucceeded(ExecutionEvent event) {
		if(isCalEnabled) {
			CALLoggerUtil.endCALTransaction(projectTransaction, "0");
		}
	}

	@Override
	public void projectFailed(ExecutionEvent event) {
		if(isCalEnabled) {
			CALLoggerUtil.endCALTransaction(projectTransaction, "FAILED", event.getException().fillInStackTrace());
		}
	}
	
	@Override
	public void mojoStarted(ExecutionEvent event) {
		if(isCalEnabled) {
			mojoTransaction = CALLoggerUtil.startCALTransaction(event.getMojoExecution().getPlugin().getId(), "PLUGIN", "");
		}
	}

	@Override
	public void mojoSucceeded(ExecutionEvent event) {
		if(isCalEnabled) {
			CALLoggerUtil.endCALTransaction(mojoTransaction, "0");
		}
	}

	@Override
	public void mojoFailed(ExecutionEvent event) {
		if(isCalEnabled) {
			CALLoggerUtil.endCALTransaction(mojoTransaction, "FAILED", event.getException().getCause());
		}
	}

	@Override
	public void forkStarted(ExecutionEvent event) {
		
	}

	@Override
	public void forkSucceeded(ExecutionEvent event) {
		
	}

	@Override
	public void forkFailed(ExecutionEvent event) {
		
	}

	@Override
	public void mojoSkipped(ExecutionEvent event) {
		
	}



	@Override
	public void forkedProjectStarted(ExecutionEvent event) {
		
	}

	@Override
	public void forkedProjectSucceeded(ExecutionEvent event) {
		
	}

	@Override
	public void forkedProjectFailed(ExecutionEvent event) {
		
	}
	
	
}
