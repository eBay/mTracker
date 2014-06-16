package com.ebay.build.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.ebay.build.core.utils.FileUtils;
import com.ebay.build.dal.AssemblyBreakdown;
import com.ebay.build.dal.SessionJDBCTemplate;

public class AssemblyLogLoader {
	
	private SessionJDBCTemplate sessionJDBCTemplate;
	
	public void setSessionJDBCTemplate(SessionJDBCTemplate jdbcTemplate) {
		this.sessionJDBCTemplate = jdbcTemplate;
	}
	
	public void load(File file) {
		AssemblyBreakdown ab = parseFile(file);
		if (ab != null) {
			List<AssemblyBreakdown> results = new ArrayList<AssemblyBreakdown>();
			results.add(ab);
			this.sessionJDBCTemplate.batchUpdateAssemblyBreakdown(results);
			FileUtils.renameDoneFile(file);
		} else {
			System.out.println("Can not load file " + file);
		}
	}

	protected AssemblyBreakdown parseFile(File file) {
		Properties prop = loadProperties(file);
		return extractValues(prop);
	}
	
	private AssemblyBreakdown extractValues(Properties prop) {
		AssemblyBreakdown ab = new AssemblyBreakdown();
		
		String jenkinsURL = prop.getProperty("jenkins.url");
		if (jenkinsURL == null) {
			return null;
		}
		
		try {
			System.out.println("== jenkins url: " + URLDecoder.decode(jenkinsURL, "UTF-8"));
			System.out.println("== stack: " + prop.getProperty("stack"));
			ab.setJenkinsUrl(URLDecoder.decode(jenkinsURL, "UTF-8"));
			ab.setPackageDuration(Integer.parseInt(prop.getProperty("package")));
			ab.setUploadDuration(Integer.parseInt(prop.getProperty("upload")));
			ab.setServiceDuration(Integer.parseInt(prop.getProperty("service")));
			ab.setStack(prop.getProperty("stack"));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return ab;
	}
	
	private Properties loadProperties(File file) {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(file);
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}
}
