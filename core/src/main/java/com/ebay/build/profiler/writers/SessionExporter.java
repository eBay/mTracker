package com.ebay.build.profiler.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.utils.StringUtils;

public class SessionExporter {

	public List<File> process(Session session) {
		return process(session, genTargetFolder());
	}
	
	public List<File> process(Session session, File targetFolder) {
		File targetFile = new File(targetFolder, getSessionLogFileName(session));

		System.out.println("[INFO] Dump build tracking session to " + targetFile);
		
		List<File> files = new ArrayList<File>();

		writeToFile(targetFile, session.toString());
		
		files.add(targetFile);
		
		if (!StringUtils.isEmpty(session.getFullStackTrace())) {
			File stackTraceFile = new File(genTargetFolder(), targetFile.getName() + ".stacktrace");
			System.out.println("[INFO] Dump build tracking session stacktrace to " + stackTraceFile);
			writeToFile(stackTraceFile, session.getFullStackTrace());
			files.add(stackTraceFile);
		}
		return files;
	}
	
	private void writeToFile(File targetFile, String body) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(targetFile);
			fileWriter.write(body);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private String getSessionLogFileName(Session session) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(session.getAppName()).append("--").append(session.getMachineName());
		sBuffer.append("--").append(session.getStartTime().getTime()).append(".log");
		return sBuffer.toString();
	}

	protected File genTargetFolder() {
		File targetFolder = new File("/var/lib/jenkins/raptor.build.tracking/logs");
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
}
