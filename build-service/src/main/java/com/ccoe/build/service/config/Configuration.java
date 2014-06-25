package com.ccoe.build.service.config;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {

	private boolean globalSwitch = true;
	private int statusCode = 200;
	private String contacts;
	private String site;
	private String errorMessage;

	public boolean isGlobalSwitch() {
		return globalSwitch;
	}

	public void setGlobalSwitch(boolean globalSwitch) {
		this.globalSwitch = globalSwitch;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}