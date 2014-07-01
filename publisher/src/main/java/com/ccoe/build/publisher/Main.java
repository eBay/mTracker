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

public class Main {
	
	public static void main(String[] args) {
		String logDir = "F:\\var\\lib\\jenkins\\maven.build.tracking\\logs";
		int retensionDays = 14;
		if (args != null && args.length > 0) {
			 logDir = args[0];
			 if (args.length == 2) {
				 retensionDays = Integer.parseInt(args[1]);
			 }
		}
		
		File targetFolder = getTargetFolder(logDir);
		
		if (targetFolder.exists()) {
			PublisherConfig config = new PublisherConfig().targetFolder(targetFolder).retensionDays(retensionDays);
			new LogPublisher(config).publish();
			System.out.println("======= LogPubliser DONE =======");
		}
	}
	
	private static File getTargetFolder(String logDir) {
		if (logDir == null) {
			logDir = "/var/lib/jenkins/maven.build.tracking/logs";
		}
		return new File(logDir);
	}
}

