/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.profiler.profile;

import java.util.Date;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;

import com.ccoe.build.core.model.Plugin;
import com.ccoe.build.profiler.util.Timer;

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
	
	private Plugin plugin = new Plugin();

	public MojoProfile(Context c, MojoExecution mojoExecution, ExecutionEvent event) {
		super(new Timer(), event, c);
		
		this.mojoExecution = mojoExecution;
		this.pluginGroupID = mojoExecution.getGroupId();
		this.pluginArtifactID = mojoExecution.getArtifactId();
		this.pluginVersion = mojoExecution.getVersion();
		this.pluginExecutionId = mojoExecution.getExecutionId();
		
		this.event = event;
		
		// get the configuration of the plugin if the group id matches the KEY.
		// please replace the KEY
		String configuration = "";
		if (mojoExecution.getPlugin().getConfiguration() != null 
				&& mojoExecution.getPlugin().getGroupId().contains("KEY")) {
			configuration = mojoExecution.getPlugin().getConfiguration().toString();
		}
		String payload = " (" + pluginExecutionId + ")  " + configuration;
		
		if (getSession() != null) {
			plugin.setGroupId(pluginGroupID);
			plugin.setArtifactId(pluginArtifactID);
			plugin.setVersion(pluginVersion);
			plugin.setPluginKey(mojoExecution.getPlugin().getId());
			plugin.setStartTime(new Date(this.getTimer().getStartTime()));
			plugin.setPayload(payload);
			plugin.setExecutionId(pluginExecutionId);
			if (getSession().getCurrentProject().getPhases().size() > 0) {
				getSession().getCurrentProject().getLastPhase().getPlugins().add(plugin);
			}
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
		
		String status = this.endTransaction();
		
		plugin.setDuration(this.getElapsedTime());
		plugin.setStatus(status);
	}
}
