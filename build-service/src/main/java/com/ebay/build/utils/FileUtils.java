package com.ebay.build.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	
	public static final String DONE_EXT = ".DONE";
	public static final String XML_EXT = ".xml";
	public static final File QUEUE_DIR = new File("/raptor.build.service/track/queue");

	public static void diskClean(File targetFolder, int retensionDays) {
		File[] doneFiles = loadDoneFiles(targetFolder);
		List<File> filesToDelete = new ArrayList<File>();
		for (File file : doneFiles) {
			long diff = System.currentTimeMillis() - file.lastModified();
			long interval = retensionDays * 24 * 60 * 60 * 1000;
			
			if (diff > interval) {
				filesToDelete.add(file);
			}
		}
		
		System.out.println("[INFO] Cleaning up " + filesToDelete.size() + " DONE files older than " + retensionDays + " days in target folder: " + targetFolder);
		for (File file : filesToDelete) {
			file.delete();
		}
	}
	
	public static File[] loadDoneFiles(File targetFolder) {
		return loadFiles(targetFolder, DONE_EXT);
	}
	
	public static File[] loadFiles(final File targetFolder, final String ext) {
		return targetFolder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(ext);
			}
		});
		
	}
	
	public static void renameDoneFile(File file) {
		if (!file.exists()) {
			return;
		}
		File dest = new File(file.getParent(), file.getName() + DONE_EXT);
		boolean success = file.renameTo(dest);
		if (success) {
			System.out.println("[INFO] Rename Session LOG " + dest);
		} else {
			System.out.println("[WARNING] Failed rename session LOG to " + dest);
		}
	}
}
