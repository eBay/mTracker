package com.ebay.build.cal.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Session {
	private Pool pool;
	
	private Date startTime;
	private long duration;
	private String userName;
	private String status;
	private String mavenVersion;
	private String javaVersion;
	private String environment;
	
	private List<String> goals;
	private Map<String, Project> projects =  new HashMap<String, Project>();
	
	public Pool getPool() {
		return pool;
	}
	public void setPool(Pool pool) {
		this.pool = pool;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMavenVersion() {
		return mavenVersion;
	}
	public void setMavenVersion(String mavenVersion) {
		this.mavenVersion = mavenVersion;
	}
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public List<String> getGoals() {
		return goals;
	}
	public void setGoals(List<String> goals) {
		this.goals = goals;
	}
	public Map<String, Project> getProjects() {
		return projects;
	}
	public String getJavaVersion() {
		return javaVersion;
	}
	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}
}
