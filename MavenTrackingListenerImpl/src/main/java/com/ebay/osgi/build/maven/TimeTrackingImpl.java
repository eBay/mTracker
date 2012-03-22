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
	
	
	public TimeTrackingImpl() {
		URL calConfig = getClass().getClassLoader().getResource("cal.properties");
		CALLoggerUtil.initialize(calConfig);
	}

	@Override
	public void projectDiscoveryStarted(ExecutionEvent event) {
		
		StringBuilder data = new StringBuilder();
		
		data.append("goals=").append(event.getSession().getGoals().toString()).append(" ").append(event.getSession().getSystemProperties().toString());
		
		try {
			data.append(";machine=").append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {}
		
		data.append(";username=").append(System.getProperty("user.name"));
		
		File gitMeta = GitUtil.findGitRepository(event.getSession().getTopLevelProject().getBasedir());
		if(gitMeta != null && gitMeta.exists()) {
			File gitConfig = new File(gitMeta, "config");
			String gitURL =  GitUtil.getRepoName(gitConfig);
			if(gitURL != null) {
				data.append(";git_repo=").append(gitURL);
			}
		}

		if(System.getenv("BUILD_URL") != null) {
             data.append(";jenkins_url=").append(System.getenv("BUILD_URL"));
             data.append(";git_branch").append(System.getenv("GIT_BRANCH"));
         }
		discoveryTransaction = CALLoggerUtil.startCALTransaction("Project Discovery" , data.toString());
	}


	@Override
	public void sessionStarted(ExecutionEvent event) {
		sessionTransaction = CALLoggerUtil.startCALTransaction(event.getSession().getTopLevelProject().getId(), "SESSION", event.getSession().getStartTime().toString());
	}

	@Override
	public void sessionEnded(ExecutionEvent event) {
		
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

	@Override
	public void projectSkipped(ExecutionEvent event) {
		CALLoggerUtil.logCALEvent("SKIPPED", event.getProject().getId());
	}

	@Override
	public void projectStarted(ExecutionEvent event) {
		projectTransaction = CALLoggerUtil.startCALTransaction(event.getProject().getId(),"PROJECT", "");
	}

	@Override
	public void projectSucceeded(ExecutionEvent event) {
		CALLoggerUtil.endCALTransaction(projectTransaction, "0");
	}

	@Override
	public void projectFailed(ExecutionEvent event) {
		CALLoggerUtil.endCALTransaction(projectTransaction, "FAILED", event.getException().fillInStackTrace());
	}
	
	@Override
	public void mojoStarted(ExecutionEvent event) {
		mojoTransaction = CALLoggerUtil.startCALTransaction(event.getMojoExecution().getPlugin().getId(), "PLUGIN", "");
	}

	@Override
	public void mojoSucceeded(ExecutionEvent event) {
		CALLoggerUtil.endCALTransaction(mojoTransaction, "0");
	}

	@Override
	public void mojoFailed(ExecutionEvent event) {
		CALLoggerUtil.endCALTransaction(mojoTransaction, "FAILED", event.getException().getCause());
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
