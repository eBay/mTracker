package com.ebay.build.profiler.publisher;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class BuildTrackingVersionUpdater {
	
//	private final static String BUILD_PROFILER_VERSION_PATTERN = "BuildProfiler-{core.version}-M2_{maven.version}.jar";
//	private String currentVersion;
//	private String mavenRootDir;
//	private List<String> mavenVersions;
//	private String coreVesion;
//	
//	
//	public String getCurrentVersion() {
//		
//	}
//	
//	public String getUpdateSite() {
//		
//	}
//	
//	public String getUpgradeVersions() {
//		
//	}
//	
//	public String getMavenRoot() {
//		
//	}
//	
//	public boolean backupPreviousVersion() {
//		
//	}
//	
//	public boolean installNewVersion() {
//		
//	}
//	
//	public void restorePreviousVersion() {
//		
//	}
//	
//	private String getHostName() {
//		try {
//			return InetAddress.getLocalHost().getHostName();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		return "HOST_NAME_NOT_RESOLVED";
//	}
//	
//	public List<File> getBackupFiles() {
//		List<File> results = new ArrayList<File>();
//		for (String mavenVersion : mavenVersions) {
//			
//		}
//	}
//	
//	private boolean isWindows() {
//		if (System.getProperty("os.name").startsWith("Windows")) {
//			return true;
//		}
//		return false;
//	}
//	
//	public void upgrade() {
//		if (isWindows()) {
//			System.out.println(getHostName() + " is a windows box, not support upgrade tracking version.");
//			return;
//		}
//		if (backupPreviousVersion()) {
//			if (!installNewVersion()) {
//				restorePreviousVersion();
//			}
//		}
//	}
}
