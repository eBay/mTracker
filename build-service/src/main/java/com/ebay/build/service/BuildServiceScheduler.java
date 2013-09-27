package com.ebay.build.service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ebay.build.alerts.pfdash.PfDashScheduler;
import com.ebay.build.persistent.healthcheck.scheduler.HealthTrackScheduler;
import com.ebay.build.reliability.ReliabilityEmailScheduler;
import com.ebay.build.service.config.BuildServiceConfig;

public class BuildServiceScheduler implements ServletContextListener {
	public static File contextPath;
	
	public static void setContextPath(String contextPath) {
		File outputPath = new File(contextPath);
		
		if (!outputPath.exists()) {
			outputPath.mkdirs();
		}
		
		BuildServiceScheduler.contextPath = outputPath.getAbsoluteFile();
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("BuildServiceScheduler exit.");
	}

	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		String path = contextEvent.getServletContext().getRealPath("generated-source");
		setContextPath(path);
		
		System.out.println("BuildServiceScheduler init start. output path: " + path);
		HealthTrackScheduler healthTrackScheduler = new HealthTrackScheduler();
		ReliabilityEmailScheduler reliabilityScheduler = new ReliabilityEmailScheduler();
		PfDashScheduler pfDashScheduler = new PfDashScheduler();
		if (isSchedulerEnabled()) {
			try {
				healthTrackScheduler.run();
				reliabilityScheduler.run();
				pfDashScheduler.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Scheduler is disabled on this server.");
		}
	}

	public boolean isSchedulerEnabled() {
		String serverHostName = getHostName();
		String siteService = new BuildServiceConfig().get("com.ebay.build.reliability.email.scheduler").getSite();
		if (serverHostName.equals(siteService)) {
			return true;
		}
		return false;
	}
	
	public String getHostName() {
		InetAddress netAddress = null;
		try {
			netAddress = InetAddress.getLocalHost();
			if (null == netAddress) {
				return null;
			}			
			String name = netAddress.getHostName();
			return name;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
				
		return null;
	}
	
}
