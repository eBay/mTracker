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

package com.ccoe.build.core.model;

import java.util.HashMap;
import java.util.Map;

import com.ccoe.build.core.utils.DateUtils;
import com.ccoe.build.core.utils.StringUtils;

public class Session extends TrackingModel {
	private String appName;
	private String machineName;
	
	private String userName;
	private String status;
	private String mavenVersion;
	private String javaVersion;
	private String environment;
	private String gitUrl;
	private String gitBranch;
	private String jenkinsUrl; 
	
	private String goals;
	private Map<String, Project> projects =  new HashMap<String, Project>();
	
	private Project currentProject;
	
	private String exceptionMessage;
	private String fullStackTrace;
	
	private String payload;
	private String category;
	
	private String filter;
	
	private int id;
	
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
	public String getJenkinsUrl() {
		if (StringUtils.isEmpty(jenkinsUrl)) {
			return "N/A";
		}
		return jenkinsUrl;
	}
	public void setJenkinsUrl(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}
	
	public String toString() {
		StringBuffer sBuffer = new StringBuffer();

		appendLine(sBuffer, "SQLLog for " + getAppName() + "-MavenBuild:" + getMachineName());
		appendLine(sBuffer, "Environment: maven-build-tracking");
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
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
}
