package com.ebay.build.profiler.publisher;

import java.io.File;

public class Main {
	
	public static void main(String[] args) {
		String baseDir = null;
		if (args != null && args.length == 1) {
			 baseDir = args[0];
		}
		LogPublisher publisher = new LogPublisher();
		publisher.process(genTargetFolder(baseDir));
	}
	
	private static File genTargetFolder(String baseDir) {
		if (baseDir == null) {
			baseDir = "/tmp";
		}
		File targetFolder = new File(baseDir, "raptor.build.tracking/logs");
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
}

