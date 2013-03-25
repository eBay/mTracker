package com.ebay.build.profiler.publisher;

import java.io.File;

public class Main {
	
	public static void main(String[] args) {
		String logDir = null;
		int retensionDays = 14;
		if (args != null && args.length > 0) {
			 logDir = args[0];
			 if (args.length == 2) {
				 retensionDays = Integer.parseInt(args[1]);
			 }
		}
		LogPublisher publisher = new LogPublisher();
		publisher.process(genTargetFolder(logDir), retensionDays);
	}
	
	private static File genTargetFolder(String logDir) {
		if (logDir == null) {
			logDir = "/var/lib/jenkins/raptor.build.tracking/logs";
		}
		File targetFolder = new File(logDir);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
}

