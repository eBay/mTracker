package com.ebay.build.cal.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Project {
	private Pool pool;
	
	private String name;
	private String groupId;
	private String artifactId;
	private String type;
	private String version;
	
	private Date startTime;
	private long duration;
	private String status;
	
	private List<Phase> phases = new ArrayList<Phase>();
	
	
	public Pool getPool() {
		return pool;
	}
	public void setPool(Pool pool) {
		this.pool = pool;
	}
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
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long d) {
		this.duration = d;
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
}
