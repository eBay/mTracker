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

package com.ccoe.build.service;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.ccoe.build.alerts.devx.DevxScheduler;
import com.ccoe.build.alerts.pfdash.PfDashScheduler;
import com.ccoe.build.service.config.BuildServiceConfig;
import com.ccoe.build.tracking.TrackingScheduler;

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
		
		if (isSchedulerEnabled()) {
			PfDashScheduler pfDashScheduler = new PfDashScheduler();
			DevxScheduler devxScheduler = new DevxScheduler();

			try {
				pfDashScheduler.run();
				devxScheduler.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("Scheduler is disabled on this server.");
			
			TrackingScheduler tScheduler = new TrackingScheduler();
            // TODO:
            //      we enable this on the standby server,
            //      this is for test purpose, should be moved to the if block
            try {
                tScheduler.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
		
	}

	public boolean isSchedulerEnabled() {
		String serverHostName = getHostName();
		if (serverHostName == null) {
			return false;
		} 
		try {
			String siteService = new BuildServiceConfig().get("com.ccoe.build.reliability.email.scheduler").getSite();
			if (serverHostName.equals(siteService)) {
				return true;
			}
		} catch (Exception e) {
			System.out.println("Build Service Scheduler can not access Build Service due to " + e.getMessage());
		}
		return false;
	}
	
	public static String getHostName() {
		InetAddress netAddress = null;
		try {
			netAddress = InetAddress.getLocalHost();
			if (null == netAddress) {
				return null;
			}			
			String name = netAddress.getCanonicalHostName();
			return name;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
				
		return null;
	}
	
}
