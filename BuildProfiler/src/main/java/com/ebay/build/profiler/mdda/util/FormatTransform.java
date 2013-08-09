package com.ebay.build.profiler.mdda.util;

import com.ebay.build.profiler.mdda.bean.DArtifact;
import com.ebay.build.profiler.utils.StringUtils;


public class FormatTransform {

	public static String transform2(String test) {
		test = test.replace(".", "/");
		return test;
	}
	
	// source :    central (http://...., release) 
	// 
	public static void parseRepository(String source, DArtifact artifact) {
		String pattern = "\\((.*)\\)";
		String repoString = StringUtils.getFirstFound(source, pattern, false);
		if (repoString.indexOf(",") != -1) {
			String[] repoArray = repoString.split(",");
			artifact.setRepositoryURL(repoArray[0].trim());
			artifact.setRepositoryId(repoArray[1].trim());
		}
	}
}
