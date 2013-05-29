package com.ebay.build.profiler.writers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.utils.StringUtils;

public class SessionExporter {

	public void process(Session session) {
		File targetFile = new File(genTargetFolder(), getSessionLogFileName(session));

		System.out.println("[INFO] Dump build tracking session to " + targetFile);
		
		writeToFile(targetFile, session.toString());
		
		if (!StringUtils.isEmpty(session.getFullStackTrace())) {
			File stackTraceFile = new File(genTargetFolder(), targetFile.getName() + ".stacktrace");
			System.out.println("[INFO] Dump build tracking session stacktrace to " + stackTraceFile);
			writeToFile(stackTraceFile, session.getFullStackTrace());
		}
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
		sBuffer.append(session.getPool().getName()).append("--").append(session.getPool().getMachine().getName());
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
