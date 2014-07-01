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

package com.ccoe.build.publisher;

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
