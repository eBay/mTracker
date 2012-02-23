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

package com.ccoe.build.tracking;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ccoe.build.utils.ServiceConfig;

public class TrackingScheduler {

	public void run() throws Exception {
        System.out.println("[INFO] Running TrackingScheduler...   ");
		// Grab the Scheduler instance from the Factory
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		// define the job and tie it to our BatchUpdateReportJob class
		JobDetail batchUpdateDurationJob = newJob(BatchUpdateDurationJob.class)
				.withIdentity("batchUpdateDurationJob", "group1").build();
		
		JobDetail batchUpdateCategoryJob = newJob(BatchUpdateCategoryJob.class)
				.withIdentity("batchUpdateCategoryJob", "group1").build();

    	// Trigger the job to run now, and then repeat every 24 hours
        Trigger budTrigger = newTrigger()
                .withIdentity("batchUpdateDurationTrig", "group1")
                .withSchedule(cronSchedule(ServiceConfig.get("scheduler.batchUpdateDuration.time"))).build();
        
        Trigger bucTrigger = newTrigger()
                .withIdentity("batchUpdateCategoryTrig", "group1")
                .withSchedule(cronSchedule(ServiceConfig.get("scheduler.batchUpdateCategory.time"))).build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(batchUpdateDurationJob, budTrigger);
		scheduler.scheduleJob(batchUpdateCategoryJob, bucTrigger);

		// and start it off
		scheduler.start();
		System.out.println("[INFO] TrackingScheduler started.");
	}

	public static void main(String[] args) {
		TrackingScheduler scheduler = new TrackingScheduler();
		try {
			scheduler.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
