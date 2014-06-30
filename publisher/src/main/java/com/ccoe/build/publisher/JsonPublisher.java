package com.ccoe.build.publisher;

import java.io.File;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.JsonProcessor;
import com.ccoe.build.core.readers.ReaderProcessor;

public class JsonPublisher extends AbstractPublisher {
	
	ReaderProcessor processor = new JsonProcessor();
	
	public JsonPublisher() {
		super();
	}
	
	public JsonPublisher(PublisherConfig c) {
		super(c);
	}

	public ReaderProcessor getProcessor() {
		return processor;
	}
	
	public Session process(File file) {
		return super.process(file);
	}
	
	public String getTargetFileExtension() {
		return ".json";
	}

	public boolean isValidSession(String content) {
		// TODO
		return true;
	}
	
	public static void main(String[] args) {
		String logDir = null;
		int retensionDays = 14;
		if (args != null && args.length > 0) {
			 logDir = args[0];
			 if (args.length == 2) {
				 retensionDays = Integer.parseInt(args[1]);
			 }
		}
		
		File targetFolder = getTargetFolder(logDir);
		
		if (targetFolder.exists()) {
			new JsonPublisher(new PublisherConfig().targetFolder(targetFolder)
					.retensionDays(retensionDays)).publish();
		}
	}
	
	private static File getTargetFolder(String logDir) {
		if (logDir == null) {
			logDir = "/maven.build.service/track/queue/build";
		}
		return new File(logDir);
	}
}
