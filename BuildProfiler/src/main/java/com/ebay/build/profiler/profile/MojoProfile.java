package com.ebay.build.profiler.profile;

import java.util.Date;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;

import com.ebay.build.profiler.model.Plugin;
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
	
	private Plugin plugin = new Plugin();

	public MojoProfile(Context c, MojoExecution mojoExecution, ExecutionEvent event) {
		super(new Timer(), event, c);
		
		this.mojoExecution = mojoExecution;
		this.pluginGroupID = mojoExecution.getGroupId();
		this.pluginArtifactID = mojoExecution.getArtifactId();
		this.pluginVersion = mojoExecution.getVersion();
		this.pluginExecutionId = mojoExecution.getExecutionId();
		
		this.event = event;
		
		String configuration = "";
		if (mojoExecution.getPlugin().getConfiguration() != null 
				&& mojoExecution.getPlugin().getGroupId().contains("ebay")) {
			configuration = mojoExecution.getPlugin().getConfiguration().toString();
		}
		String payload = " (" + pluginExecutionId + ")  " + configuration;
		
		if (this.isInJekins()) {
			plugin.setGroupId(pluginGroupID);
			plugin.setArtifactId(pluginArtifactID);
			plugin.setVersion(pluginVersion);
			plugin.setPluginKey(mojoExecution.getPlugin().getId());
			plugin.setStartTime(new Date(this.getTimer().getStartTime()));
			plugin.setPayload(payload);
			plugin.setExecutionId(pluginExecutionId);
			getSession().getCurrentProject().getLastPhase().getPlugins().add(plugin);
		}
		
		if(isCalInitialized()) {
			mojoTransaction = calogger.startCALTransaction(mojoExecution.getPlugin().getId(), 
					"Plugin",  
					payload);
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
		super.stop();
		
		String status = this.endTransaction(mojoTransaction);
		
		if (this.isInJekins()) {
			plugin.setDuration(this.getElapsedTime());
			plugin.setStatus(status);
		}
	}
}
