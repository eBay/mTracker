package com.ebay.build.publisher;

import java.io.File;

import com.ebay.build.core.readers.ReaderProcessor;

public class AssemblyLogPublisher extends AbstractPublisher {

	private final AssemblyLogLoader assemblyLoader;
	
	public static void main(String[] args) {
		PublisherConfig config = new PublisherConfig().targetFolder(new File("F:\\var\\lib\\jenkins\\raptor.build.tracking\\logs")).retensionDays(1);
		new AssemblyLogPublisher(config).publish();
	}
	 
	public AssemblyLogPublisher(PublisherConfig c) {
		super(c);
		this.assemblyLoader = this.applicationContext.getBean("assemblyLogLoader", AssemblyLogLoader.class);
	}
	
	public void publish() {
		preProcess();
		
		File[] files = loadSessions();
		if (files == null) {
			System.out.println("No assembly logs to handle.");
			return;
		}
		
		for (File file : files) {
			assemblyLoader.load(file);
		}
	}

	public String getTargetFileExtension() {
		return ".perf";
	}

	public ReaderProcessor getProcessor() {
		return null;
	}

	public boolean isValidSession(String content) {
		return false;
	}
}
