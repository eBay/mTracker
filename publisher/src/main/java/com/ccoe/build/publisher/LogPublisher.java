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
import java.util.List;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.LineProcessor;
import com.ccoe.build.core.readers.ReaderProcessor;
import com.ccoe.build.core.utils.FileUtils;
import com.ccoe.build.core.utils.StringUtils;

public class LogPublisher extends AbstractPublisher {
	private final ReaderProcessor lineProcessor = new LineProcessor();
	
	public LogPublisher() {
		super();
	}
	
	public LogPublisher(PublisherConfig config) {
		super(config);
	}
	
	public ReaderProcessor getProcessor() {
		return lineProcessor;
	}
	
	public Session process(File file) {
		Session session = super.process(file);
		if (session != null && !StringUtils.isEmpty(session.getExceptionMessage())) {
			loadFullStackTrace(file, session);
		}
		return session;
	}
	
	public void postProcess(List<File> files, List<File> failedFiles) {
		super.postProcess(files, failedFiles);
		
		for (File file : files) {
			File stackTraceFile = new File(file.getParent(), file.getName() + ".stacktrace");
			if (stackTraceFile.exists()) {
				FileUtils.renameDoneFile(stackTraceFile);
			}
		}
		
		for (File file : failedFiles) {
			File stackTraceFile = new File(file.getParent(), file.getName() + ".stacktrace");
			if (stackTraceFile.exists()) {
				FileUtils.renameFailedFile(stackTraceFile);
			}
		}

	}
	
	private File loadFullStackTrace(File file, Session session) {
		File stackTraceFile = new File(file.getParent(), file.getName() + ".stacktrace");
		session.setFullStackTrace(FileUtils.readFile(stackTraceFile));
		return stackTraceFile;
	}

	private boolean isACompleteSession(String payload) {
		String tEndPattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s+Environment\\s+\\w+\\s+(\\d+)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(payload, tEndPattern, true);
		return found.size() > 0; 
	}

	public String getTargetFileExtension() {
		return ".log";
	}

	public boolean isValidSession(String content) {
		return isACompleteSession(content);
	}
}
