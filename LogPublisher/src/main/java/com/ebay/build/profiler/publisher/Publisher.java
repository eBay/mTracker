package com.ebay.build.profiler.publisher;

import java.io.File;
import java.util.List;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.readers.ReaderProcessor;


public interface Publisher {
	PublisherConfig getConfig();
	String getTargetFileExtension();
	
	ReaderProcessor getProcessor();
	
	void preProcess();
	boolean isValidSession(String content);
	Session process(File file);
	void postProcess(List<File> files);
	
	void publish();
}
