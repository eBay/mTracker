package com.ccoe.build.utils;

public class ServiceConfig {
	private static java.util.Properties prop = new java.util.Properties();

	private static void loadProperties() {
		// get class loader
		ClassLoader loader = ServiceConfig.class.getClassLoader();
		if (loader == null) {
			loader = ClassLoader.getSystemClassLoader();
		}
		// load application.properties located in WEB-INF/classes/
		String propFile = "application.properties";
		java.net.URL url = loader.getResource(propFile);
		try {
			prop.load(url.openStream());
		} catch (Exception e) {
			System.err
					.println("Could not load configuration file: " + propFile);
		}
	}

	public static String get(String key) {
		return prop.getProperty(key);
	}
	
	public static int getInt(String key) {
		return getInt(key, "3600");
	}
	
	public static int getInt(String key, String defaultValue) {
		return Integer.parseInt(prop.getProperty(key, defaultValue));
	}
	
	// load the properties when class is accessed
	static {
		loadProperties();
	}
}
