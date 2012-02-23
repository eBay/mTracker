package com.ebay.osgi.build.maven;

import java.net.URL;

import org.apache.maven.execution.AbstractExecutionListener;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.kernel.calwrapper.CalTransaction;
import com.ebay.osgi.build.util.CALLoggerUtil;

public class TimeTrackingImpl extends AbstractExecutionListener {
	
	private CalTransaction sessionTransaction;
	private CalTransaction projectTransaction;
	private CalTransaction mojoTransaction;
	
	
	public TimeTrackingImpl() {
		URL calConfig = getClass().getClassLoader().getResource("cal.properties");
		CALLoggerUtil.initialize(calConfig);
	}

	@Override
	public void projectDiscoveryStarted(ExecutionEvent event) {
		
	}

	@Override
	public void sessionStarted(ExecutionEvent event) {
		sessionTransaction = CALLoggerUtil.startCALTransaction("Session-" + event.getSession().getTopLevelProject().getId(), event.getSession().getGoals().toString());
	}

	@Override
	public void sessionEnded(ExecutionEvent event) {
		CALLoggerUtil.endCALTransaction(sessionTransaction, "0");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
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
		projectTransaction = CALLoggerUtil.startCALTransaction("Project-" + event.getProject().getId(), "");
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
		mojoTransaction = CALLoggerUtil.startCALTransaction(event.getMojoExecution().getPlugin().getId(), "");
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
