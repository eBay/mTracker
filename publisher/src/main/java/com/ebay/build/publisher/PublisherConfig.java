package com.ebay.build.publisher;

import java.io.File;

public class PublisherConfig {
	private File targetFolder;
	private int retensionDays;
	
	public PublisherConfig targetFolder(File targetFolder) {
		this.setTargetFolder(targetFolder);
		return this;
	}
	
	public PublisherConfig retensionDays(int retensionDays) {
		this.setRetensionDays(retensionDays);
		return this;
	}
	
	public File getTargetFolder() {
		return targetFolder;
	}
	public void setTargetFolder(File targetFolder) {
		this.targetFolder = targetFolder;
	}
	public int getRetensionDays() {
		return retensionDays;
	}
	public void setRetensionDays(int retensionDays) {
		this.retensionDays = retensionDays;
	}
}
