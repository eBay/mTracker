package com.ebay.build.tracking;

import com.ebay.build.persistent.healthcheck.scheduler.BatchUpdateReportJob;
import com.ebay.build.persistent.healthcheck.scheduler.DiskCleanJob;
import com.ebay.build.persistent.healthcheck.scheduler.EmailSummaryPageJob;
import com.ebay.build.utils.ServiceConfig;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class TrackingScheduler {

	public void run() throws Exception {
        System.out.println("[INFO] Running TrackingScheduler...   ");
		// Grab the Scheduler instance from the Factory
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		// define the job and tie it to our BatchUpdateReportJob class
		JobDetail batchUpdateJob = newJob(BatchUpdateDurationJob.class)
				.withIdentity("batchUpdateDurationJob", "group1").build();

    	// Trigger the job to run now, and then repeat every 24 hours
        Trigger dcTrigger = newTrigger()
                .withIdentity("batchUpdateDurationTrig", "group1")
                .withSchedule(cronSchedule(ServiceConfig.get("scheduler.batchUpdateDuration.time"))).build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(batchUpdateJob, dcTrigger);

		// and start it off
		scheduler.start();
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
