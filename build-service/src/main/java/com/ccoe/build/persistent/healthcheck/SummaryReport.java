package com.ccoe.build.persistent.healthcheck;


public class SummaryReport {
	
	private String jobName;
	private String gitUrl;
	private String gitBranch;
	private String buildURL;
	private int count;
	private String severity;
	private int majorCount;
	private int criticalCount;
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getGitUrl() {
		return gitUrl;
	}
	public void setGitUrl(String gitUrl) {
		this.gitUrl = gitUrl;
	}
	public String getGitBranch() {
		return gitBranch;
	}
	public void setGitBranch(String gitBranch) {
		this.gitBranch = gitBranch;
	}
	public String getBuildURL() {
		return buildURL;
	}
	public void setBuildURL(String buildURL) {
		this.buildURL = buildURL;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(String severity) {
		this.severity = severity;
	}
	public int getMajorCount() {
		return majorCount;
	}
	public void setMajorCount(int majorCount) {
		this.majorCount = majorCount;
	}
	public int getCriticalCount() {
		return criticalCount;
	}
	public void setCriticalCount(int criticalCount) {
		this.criticalCount = criticalCount;
	}
}
