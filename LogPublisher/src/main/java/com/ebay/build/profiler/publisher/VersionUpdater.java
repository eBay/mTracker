package com.ebay.build.profiler.publisher;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class VersionUpdater {
	
	private File baseDir = new File("./lib");
	
	private String projectName = "LogPublisher";
	private final Properties prop = new Properties();
	private String propFileName = "log_publisher.properties";
	
	public VersionUpdater() {
		try {
			prop.load(this.getClass().getResourceAsStream(propFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
	}
	
	public String getCurrentVersion() {
		return prop.getProperty("current.version");
	}
	
	public String getRemoteVersion() {
		try {
			URL propURL = new URL(prop.getProperty("remote.url") + propFileName);
			
			Properties remoteProp = new Properties();
			remoteProp.load(propURL.openStream());
			return remoteProp.getProperty("remote.version");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
