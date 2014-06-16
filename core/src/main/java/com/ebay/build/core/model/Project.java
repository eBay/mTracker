package com.ebay.build.core.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Project extends TrackingModel {
	private String name;
	private String groupId;
	private String artifactId;
	private String type;
	private String version;
	
	private String status;
	
	private String payload;
	
	private List<Phase> phases = new ArrayList<Phase>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public List<Phase> getPhases() {
		return phases;
	}
	
	public Phase getLastPhase() {
		return phases.get(phases.size() - 1);
	}
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();

		appendTransacionStart(sBuffer, 2, "Project", getName());
		
		for (Phase phase : getPhases()) {
			appendLine(sBuffer, phase.toString());
		}
		
		appendTransacionEnd(sBuffer, 2, "Project", getName(), getStatus(), getDuration().toString(), getPayload());
		
		return sBuffer.toString();
	}
}
