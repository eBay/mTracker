package com.ebay.build.cal.processors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.ebay.build.cal.model.Session;

public class SessionExporter {

	public void process(Session session) {
		File targetFile = new File(genTargetFolder(),
				getSessionLogFileName(session));

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(targetFile);
			fileWriter.write(session.toString());
			fileWriter.close();
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

	protected String getSessionLogFileName(Session session) {
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(session.getPool().getName()).append("--").append(session.getPool().getMachine().getName());
		sBuffer.append("--").append(session.getStartTime().getTime()).append(".log");
		return sBuffer.toString();
	}

	protected File genTargetFolder() {
		String baseDir = System.getProperty("maven.home");
		if (baseDir == null) {
			baseDir = System.getProperty("user.dir");
		}

		File targetFolder = new File(baseDir, "raptor.build.tracking.logs");
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}
}
