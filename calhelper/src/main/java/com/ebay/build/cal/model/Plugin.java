package com.ebay.build.cal.model;


public class Plugin extends TrackingModel {
	private String groupId;
	private String artifactId;
	private String version;
	
	private String status;
	private String executionId;
	
	private String phaseName;
	private String pluginKey;
	
	private int id;
	
	private String payload;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}

	public String getPhaseName() {
		return phaseName;
	}

	public void setPhaseName(String phaseName) {
		this.phaseName = phaseName;
	}

	public String getPluginKey() {
		return pluginKey;
	}

	public void setPluginKey(String pluginKey) {
		this.pluginKey = pluginKey;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();
		appendTransactionAtom(sBuffer, 4, "Plugin", getPluginKey(), this.getStatus(), getDuration().toString(), getPayload());
		return sBuffer.toString();
	}
}
