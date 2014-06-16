package com.ebay.build.publisher;

import java.io.File;
import java.util.List;

import com.ebay.build.core.model.Session;
import com.ebay.build.core.readers.ReaderProcessor;


public interface Publisher {
	PublisherConfig getConfig();
	String getTargetFileExtension();
	
	ReaderProcessor getProcessor();
	
	void preProcess();
	boolean isValidSession(String content);
	Session process(File file);
	void postProcess(List<File> files, List<File> failedFiles);
	
	void publish();
}
