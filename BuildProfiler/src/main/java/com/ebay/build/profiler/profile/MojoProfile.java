package com.ebay.build.profiler.profile;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;

import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;

/**
 * 
 * This Class holds the Mojo profiling information
 * 
 * @author kmuralidharan
 * 
 */

public class MojoProfile extends Profile {

	private MojoExecution mojoExecution;

	private String status;
	private String executionMsg;

	private String pluginGroupID;
	private String pluginArtifactID;
	private String pluginVersion;
	private String pluginExecutionId;
	
	private CalTransaction mojoTransaction;
	private ExecutionEvent event;

	public MojoProfile(MojoExecution mojoExecution) {
		this(mojoExecution, null);
	}
	
	public MojoProfile(MojoExecution mojoExecution, ExecutionEvent event) {
		super(new Timer());
		
		this.mojoExecution = mojoExecution;
		this.pluginGroupID = mojoExecution.getGroupId();
		this.pluginArtifactID = mojoExecution.getArtifactId();
		this.pluginVersion = mojoExecution.getVersion();
		this.pluginExecutionId = mojoExecution.getExecutionId();
		
		this.event = event;
		
		if(calogger.isCalInitialized()) {
			String configuration = "";
			if (mojoExecution.getPlugin().getConfiguration() != null 
					&& mojoExecution.getPlugin().getGroupId().contains("ebay")) {
				configuration = mojoExecution.getPlugin().getConfiguration().toString();
			}
			mojoTransaction = calogger.startCALTransaction(mojoExecution.getPlugin().getId(), 
					"Plugin",  
					" (" + pluginExecutionId + ")  " + configuration);
		}
	}

	public String getPluginGroupID() {
		return pluginGroupID;
	}

	public String getPluginArtifactID() {
		return pluginArtifactID;
	}

	public String getPluginVersion() {
		return pluginVersion;
	}

	public String getPluginExecutionId() {
		return pluginExecutionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String executionStatus) {
		this.status = executionStatus;
	}

	public void setExecutionMsg(String executionMsg) {
		this.executionMsg = executionMsg;
	}

	public String getExecutionMsg() {
		return executionMsg;
	}

	public String getId() {
		return new StringBuilder().append(pluginGroupID).append(":")
				.append(pluginArtifactID).append(":").append(pluginVersion)
				.append(" (").append(pluginExecutionId).append(") ").toString();
	}

	@Override
	public void stop() {
		if(mojoTransaction != null) {
			if(event.getSession().getResult().getExceptions().size() > 0) {
				calogger.endCALTransaction(mojoTransaction,"FAILED", event.getException());
			} else {
				calogger.endCALTransaction(mojoTransaction, "0");
			}
		}
		
		
		super.stop();
	}
}
