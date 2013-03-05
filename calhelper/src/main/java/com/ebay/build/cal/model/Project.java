package com.ebay.build.cal.model;

import java.util.Date;
import java.util.List;

public class Project {
	private Pool pool;
	
	private String name;
	private String version;
	
	private Date startTime;
	private long duration;
	private String status;
	
	private List<Phase> phases;
	
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
	public void setPhases(List<Phase> phases) {
		this.phases = phases;
	}
}
