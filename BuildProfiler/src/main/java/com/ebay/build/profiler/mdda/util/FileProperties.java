package com.ebay.build.profiler.mdda.util;

import java.io.File;

public class FileProperties {

	public static final String ROOT_DIR = "/var/lib/jenkins/raptor.build.tracking/mdda/";
	public static final String DEP_CACHE_LIST_FILE = "dcl.xml";
	public static final String REMOTE_REPO_CACHE_FILE = "repo.txt";
	public static final String REMOTE_REPO_CACHE_MD5_FILE = "repo_md5.txt";
	
	private String appName;
	
	public FileProperties(String appName) {
		this.appName = appName;
		File rootfolder = new File(ROOT_DIR, appName);
		if (!rootfolder.exists()) {
			rootfolder.mkdirs();
		}
	}
	
	public String getRootDir() {
		return ROOT_DIR;
	}

	public File getRemoteRepoCacheFile() {
		return new File(ROOT_DIR, this.appName + File.separator + REMOTE_REPO_CACHE_FILE);
	}

	public File getRemoteRepoCacheMd5File() {
		return new File(ROOT_DIR, this.appName + File.separator + REMOTE_REPO_CACHE_MD5_FILE);
	}

	public File getDepCacheListFile() {
		return new File(ROOT_DIR, this.appName + File.separator + DEP_CACHE_LIST_FILE);
	}
}
