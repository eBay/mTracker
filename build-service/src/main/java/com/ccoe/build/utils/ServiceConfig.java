/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

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
