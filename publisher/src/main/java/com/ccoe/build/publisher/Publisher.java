package com.ccoe.build.publisher;

import java.io.File;
import java.util.List;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.ReaderProcessor;


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
