package com.ebay.build.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ebay.build.persistent.healthcheck.scheduler.HealthTrackScheduler;
import com.ebay.build.reliability.ReliabilityEmailScheduler;
import com.ebay.build.service.config.BuildServiceConfig;

public class BuildServiceScheduler implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("BuildServiceScheduler exit.");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("BuildServiceScheduler init start.");
		HealthTrackScheduler healthTrackScheduler = new HealthTrackScheduler();
		ReliabilityEmailScheduler reliabilityScheduler = new ReliabilityEmailScheduler();
		if (isSchedulerEnabled()) {
			try {
				healthTrackScheduler.run();

				reliabilityScheduler.run();

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
