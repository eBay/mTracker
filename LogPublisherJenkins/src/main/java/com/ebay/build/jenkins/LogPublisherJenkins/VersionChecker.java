package com.ebay.build.jenkins.LogPublisherJenkins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class VersionChecker {
	private final static String REMOTE_URL = "https://github.scm.corp.ebay.com/DevExTech/maven-time-tracking/raw/embededCal/LogPublisher/bin/";	
	private final static String KEY = "LogPublisher-Jar-KEY";
	private final static String REMOTE_KEY = REMOTE_URL + KEY;
	
	private final File root;
	private final File baseDir ;
	
	private final File logsDir;
	private final File libDir;
	
	private final File buildFile;
	
	public VersionChecker(File r) {
		this.root = r;
		this.baseDir = new File(root, "raptor.build.tracking");
		this.logsDir = new File(baseDir, "logs");
		this.libDir = new File(baseDir, "lib");
		this.buildFile = new File(baseDir, "log_publisher.xml");
	}
	
	public String getProperties() {
		createDirs();

		if (!isBuildFileExists()) {
			if (!downloadBuildFile()) {
				// TODO log the failure
				return null;
			}
		}

		String remoteVersion = getRemoteVersion();
		String currentVersion = getCurrentVersion();

		// if the version changed, better to download the build xml file again.
		if (!remoteVersion.equals(currentVersion)) {
			if (!downloadBuildFile()) {
				// TODO log the failure
				return null;
			}
		}

		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append("latest.version=").append(remoteVersion).append("\n");
		sBuffer.append("current.version=").append(currentVersion).append("\n");

		return sBuffer.toString();
	}
	
	public File getBuildFile() {
		return this.buildFile;
	}

	private boolean downloadBuildFile() {
		try {
			URL remoteBuildFileURL = new URL(REMOTE_URL + this.buildFile.getName());
			IOUtils.copy(remoteBuildFileURL.openStream(), new FileOutputStream(this.buildFile));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean isBuildFileExists() {
		return this.buildFile.exists();
	}
	
	private String getRemoteVersion() {
		try {
		URL remoteVersionURL = new URL(REMOTE_KEY);
		return (String) IOUtils.readLines(remoteVersionURL.openStream()).get(0);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO handling the exception
		}
		return "";
	}
	
	private String getCurrentVersion() {
		File versionFile = new File(libDir, KEY);
		try {
			
			return (String) IOUtils.readLines(new FileInputStream(versionFile)).get(0);
			
		} catch (FileNotFoundException e) {
			System.err.println("[ERROR] File Not Found "+ versionFile);
		} catch (IOException e) {
			System.err.println("[ERROR] File Read Exception " + versionFile);
		}
		return "";
	}

	private void createDirs() {
		if (!logsDir.exists()) {
			if (!logsDir.mkdirs()) {
				System.out.println(logsDir + " creation fail!");
			}
		}
		
		if (!libDir.exists()) {
			if (!libDir.mkdirs()) {
				System.out.println(libDir + " creation fail!");
			}
		}
	}
}
