package com.ccoe.build.core.writers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.utils.FileUtils;
import com.ccoe.build.core.utils.StringUtils;

public class SessionExporter {

	public List<File> process(Session session) {
		return process(session, genTargetFolder());
	}
	
	public List<File> process(Session session, File targetFolder) {
		File targetFile = new File(targetFolder, getSessionLogFileName(session));

		System.out.println("[INFO] Dump build tracking session to " + targetFile);
		
		List<File> files = new ArrayList<File>();

		FileUtils.writeToFile(targetFile, session.toString());
		
		files.add(targetFile);
		
		if (!StringUtils.isEmpty(session.getFullStackTrace())) {
			File stackTraceFile = new File(genTargetFolder(), targetFile.getName() + ".stacktrace");
			System.out.println("[INFO] Dump build tracking session stacktrace to " + stackTraceFile);
			FileUtils.writeToFile(stackTraceFile, session.getFullStackTrace());
			files.add(stackTraceFile);
		}
		return files;
	}
	
	private String getSessionLogFileName(Session session) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(session.getAppName()).append("--").append(session.getMachineName());
		sBuffer.append("--").append(session.getStartTime().getTime()).append(".log");
		return sBuffer.toString();
	}

	protected File genTargetFolder() {
		File targetFolder = new File("/var/lib/jenkins/maven.build.tracking/logs");
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
}
