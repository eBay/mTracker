package com.ccoe.build.service.config;

public class BuildServiceConfigBean {
	private boolean globalSwitch;
	private String statusCode;
	private String contacts;
	private String site;

	
	public boolean isGlobalSwitch() {
		return globalSwitch;
	}
	public void setGlobalSwitch(boolean globalSwitch) {
		this.globalSwitch = globalSwitch;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	
}
