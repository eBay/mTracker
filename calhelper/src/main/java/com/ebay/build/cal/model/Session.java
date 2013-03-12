package com.ebay.build.cal.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sonatype.aether.util.StringUtils;

public class Session {
	private Pool pool;
	
	private Date startTime;
	private long duration;
	private String userName;
	private String status;
	private String mavenVersion;
	private String javaVersion;
	private String environment;
	private String gitUrl;
	private String gitBranch;
	private String jekinsUrl; 
	
	private String goals;
	private Map<String, Project> projects =  new HashMap<String, Project>();
	
	private Project currentProject;
	
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
	public String getGoals() {
		return goals;
	}
	public void setGoals(String goals) {
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
	public Project getCurrentProject() {
		return currentProject;
	}
	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}
	public String getGitUrl() {
		if (StringUtils.isEmpty(gitUrl)) {
			return "N/A";
		}

		return gitUrl;
	}
	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}
	public String getGitBranch() {
		if (StringUtils.isEmpty(gitBranch)) {
			return "N/A";
		}
		return gitBranch;
	}
	public void setGitBranch(String gitBranch) {
		this.gitBranch = gitBranch;
	}
	public String getJekinsUrl() {
		if (StringUtils.isEmpty(jekinsUrl)) {
			return "N/A";
		}
		return jekinsUrl;
	}
	public void setJekinsUrl(String jekinsUrl) {
		this.jekinsUrl = jekinsUrl;
	}
}
