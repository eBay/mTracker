package com.ebay.build.profiler.publisher;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.ebay.build.cal.model.Session;
import com.ebay.build.cal.processors.LineProcessor;
import com.ebay.build.cal.processors.LoaderProcessor;
import com.ebay.build.cal.query.utils.StringUtils;

public class LogPublisher {
	private final LoaderProcessor loaderProcessor = new LoaderProcessor();
	private final LineProcessor pro = new LineProcessor();
	
	public void process(File targetFolder, int retensionDays) {
		try {
			System.out.println("[INFO] Cleaning up Done files older than two weeks in target folder: " + targetFolder);
			diskClean(targetFolder, retensionDays);
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("[INFO] Looking into target folder: " + targetFolder);
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
			
			if (!isACompleteSession(sBuffer.toString()))	{
				System.out.println("[INFO] Skipped " + file + " is not a complete session.");
				continue;
			}
			
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
	
	private boolean isACompleteSession(String payload) {
		String tEndPattern = "(\\d+)\\s+T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\s+Environment\\s+\\w+\\s+(\\d+)\\s+(\\d+)\\s+(.*)";
		List<String> found = StringUtils.getFound(payload, tEndPattern, true);
		return found.size() > 0; 
	}
	
	private void diskClean(File targetFolder, int retensionDays) {
		File[] doneFiles = loadDoneFiles(targetFolder);
		
		List<File> filesToDelete = new ArrayList<File>();
		for (File file : doneFiles) {
			long diff = System.currentTimeMillis() - file.lastModified();
			long interval = retensionDays * 24 * 60 * 60 * 1000;
			
			if (diff > interval) {
				filesToDelete.add(file);
			}
		}
		
		for (File file : filesToDelete) {
			file.delete();
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
		return loadFiles(targetFolder, ".log");
	}
	
	public File[] loadDoneFiles(File targetFolder) {
		return loadFiles(targetFolder, ".DONE");
	}
	
	private File[] loadFiles(final File targetFolder, final String ext) {
		return targetFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(ext);
			}
		});
		
	}
}