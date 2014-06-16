package com.ebay.build.dal;

public class AssemblyBreakdown {
	private String jenkinsUrl;
	private int packageDuration;
	private int uploadDuration;
	private int serviceDuration;
	private String stack;
	
	public String getStack() {
		return this.stack;
	}
	
	public void setStack(String s) {
		this.stack = s;
	}
	
	public String getJenkinsUrl() {
		return jenkinsUrl;
	}
	public void setJenkinsUrl(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}
	public int getPackageDuration() {
		return packageDuration;
	}
	public void setPackageDuration(int packageDuration) {
		this.packageDuration = packageDuration;
	}
	public int getUploadDuration() {
		return uploadDuration;
	}
	public void setUploadDuration(int uploadDuration) {
		this.uploadDuration = uploadDuration;
	}
	public int getServiceDuration() {
		return serviceDuration;
	}
	public void setServiceDuration(int serviceDuration) {
		this.serviceDuration = serviceDuration;
	}
}
