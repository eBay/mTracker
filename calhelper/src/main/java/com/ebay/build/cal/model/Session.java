package com.ebay.build.cal.model;

import java.util.HashMap;
import java.util.Map;

import org.sonatype.aether.util.StringUtils;

import com.ebay.build.cal.query.utils.DateUtils;

public class Session extends TrackingModel {
	private Pool pool;
	
	private String userName;
	private String status;
	private String mavenVersion;
	private String javaVersion;
	private String environment;
	private String gitUrl;
	private String gitBranch;
	private String jekinsUrl; 
	private String raptorVersion;
	private String domainVersion;
	
	private String goals;
	private Map<String, Project> projects =  new HashMap<String, Project>();
	
	private Project currentProject;
	
	private String exceptionMessage;
	private String fullStackTrace;
	
	private String payload;
	private String category;
	
	private String filter;
	
	private int id;
	
	public Pool getPool() {
		return pool;
	}
	public void setPool(Pool pool) {
		this.pool = pool;
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
	
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();

		appendLine(sBuffer, "SQLLog for " + getPool().getName() + "-MavenBuild:" + getPool().getMachine().getName());
		appendLine(sBuffer, "Environment: raptor-build-tracking");
		appendLine(sBuffer, "Start: " + DateUtils.getCALDateTimeString(getStartTime()));
		
		appendTransacionStart(sBuffer, 0, " Environment " + getEnvironment());
		appendTransacionStart(sBuffer, 1, "  URL Session");
		
		for (Project project : getProjects().values()) {
			appendLine(sBuffer, project.toString());
		}
		
		appendTransacionEnd(sBuffer, 1, "URL Session", getStatus(), getDuration().toString(), getGoals());
		appendTransacionEnd(sBuffer, 0, " Environment ", getEnvironment(), getStatus(), getDuration().toString(), getPayload());
		
		if (!StringUtils.isEmpty(this.getExceptionMessage())) {
			appendLine(sBuffer, "-----------------EXCEPTION MESSAGE-----------------");
			appendLine(sBuffer, this.getExceptionMessage());
			appendLine(sBuffer, "-----------------EXCEPTION MESSAGE-----------------");
		}
		return sBuffer.toString();
	}
	
	private String getPayload() {
		return this.payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	public void addException(String msg) {
		String pMsg = getExceptionMessage();
		if (pMsg != null) {
			pMsg = pMsg + "\n" + msg;
		} else {
			pMsg = msg;
		}
		setExceptionMessage(pMsg);
	}

	public String getFullStackTrace() {
		return fullStackTrace;
	}
	
	public void setFullStackTrace(String fullStackTrace) {
		this.fullStackTrace = fullStackTrace;
	}
	public String getRaptorVersion() {
		return raptorVersion;
	}
	public void setRaptorVersion(String raptorVersion) {
		this.raptorVersion = raptorVersion;
		if (!StringUtils.isEmpty(this.raptorVersion) && !this.payload.contains("raptor.version")) {
			this.payload += ";raptor.version=" + this.raptorVersion;
		}
	}
	public String getDomainVersion() {
		return domainVersion;
	}
	public void setDomainVersion(String domainVersion) {
		this.domainVersion = domainVersion;
		if (!StringUtils.isEmpty(this.domainVersion) && !this.payload.contains("domain.version")) {
			this.payload += ";domain.version=" + this.domainVersion;
		}
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
}
