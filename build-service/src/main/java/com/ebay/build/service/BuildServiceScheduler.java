package com.ebay.build.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ebay.build.persistent.healthcheck.scheduler.HealthTrackScheduler;

public class BuildServiceScheduler implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("BuildServiceScheduler exit.");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("BuildServiceScheduler init start.");
		HealthTrackScheduler scheduler = new HealthTrackScheduler();
		try {
			scheduler.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("BuildServiceScheduler init done.");
	}
}
