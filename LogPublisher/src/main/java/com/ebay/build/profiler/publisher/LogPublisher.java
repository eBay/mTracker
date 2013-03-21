package com.ebay.build.profiler.publisher;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.ebay.build.cal.model.Session;
import com.ebay.build.cal.processors.LineProcessor;
import com.ebay.build.cal.processors.LoaderProcessor;

public class LogPublisher {
	private final LoaderProcessor loaderProcessor = new LoaderProcessor();
	private final LineProcessor pro = new LineProcessor();
	
	public static void main(String[] args) {
		LogPublisher importer = new LogPublisher();
		//importer.process(new File("E:/bin/apache-maven-3.0.5-RaptorTimeTracking/raptor.build.tracking.logs"));
		System.out.println("[INFO] Looking for log files in " + importer.genTargetFolder());
		importer.process(importer.genTargetFolder());
	}
	
	public void process(File targetFolder) {
		File[] files = loadSessions(targetFolder);
		if (files == null || files.length == 0) {
			System.out.println("[INFO] Found 0 log files");
		}
		for (File file : files) {
			System.out.println("[INFO] loading the file " + file);
			BufferedReader br = null;
			DataInputStream in = null;
			StringBuffer sBuffer = new StringBuffer();
			
			try {
				in = new DataInputStream(new FileInputStream(file));
				br = new BufferedReader(new InputStreamReader(in));

				String sCurrentLine = null;
				
				while ((sCurrentLine = br.readLine()) != null) {
					sBuffer.append(sCurrentLine);
					sBuffer.append("\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			System.out.println("[INFO] Loading sessions from file " + file);
			List<Session> sessions = pro.process(sBuffer.toString());
			System.out.println("[INFO] found " + sessions.size() + " sessions");
			
			for (Session session : sessions) {
				System.out.println("[INFO] Store Session -- " + session.getEnvironment() + " " + session.getPool().getName() + " " +session.getStartTime());
				loaderProcessor.process(session);
				System.out.println("[INFO] Store Session -- " + session.getEnvironment() + " " + session.getStartTime() + " DONE!");
			}
			
			renameDoneFile(file);
		}
	}

	private void renameDoneFile(File file) {
		File dest = new File(file.getParent(), file.getName() + ".DONE");
		boolean success = file.renameTo(dest);
		if (success) {
			System.out.println("[INFO] Rename Session LOG " + dest);
		} else {
			System.out.println("[WARNING] Failed rename session LOG to " + dest);
		}
	}

	public File[] loadSessions(File targetFolder) {
		return targetFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".log");
			}
		});
	}

	protected File genTargetFolder() {
		File targetFolder = new File("/tmp", "raptor.build.tracking.logs");
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}
		return targetFolder;
	}

}
