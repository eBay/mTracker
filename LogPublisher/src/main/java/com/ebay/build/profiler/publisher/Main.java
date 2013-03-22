package com.ebay.build.profiler.publisher;

import java.io.File;

public class Main {
	
	public static void main(String[] args) {
		String logDir = null;
		if (args != null && args.length == 1) {
			 logDir = args[0];
		}
		LogPublisher publisher = new LogPublisher();
		publisher.process(genTargetFolder(logDir));
	}
	
	private static File genTargetFolder(String logDir) {
		if (logDir == null) {
			logDir = "/tmp/raptor.build.tracking/logs";
		}
		File targetFolder = new File(logDir);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
}

