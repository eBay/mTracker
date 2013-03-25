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
		File targetFolder = getTargetFolder(logDir);
		if (targetFolder.exists()) {
			publisher.process(targetFolder, retensionDays);
		}
	}
	
	private static File getTargetFolder(String logDir) {
		if (logDir == null) {
			logDir = "/var/lib/jenkins/raptor.build.tracking/logs";
		}
		return new File(logDir);
	}
}

