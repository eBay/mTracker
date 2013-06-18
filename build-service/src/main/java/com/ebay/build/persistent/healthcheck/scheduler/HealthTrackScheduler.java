package com.ebay.build.persistent.healthcheck.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ebay.build.utils.ServiceConfig;

public class HealthTrackScheduler {

	public void run() throws Exception {
		// Grab the Scheduler instance from the Factory
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

		// define the job and tie it to our BatchUpdateReportJob class
		JobDetail batchUpdateJob = newJob(BatchUpdateReportJob.class)
				.withIdentity("batchUpdateJob", "group1").build();

		JobDetail emailJob = newJob(EmailSummaryPageJob.class).withIdentity(
				"emailJob", "group1").build();

		JobDetail dcJob = newJob(DiskCleanJob.class).withIdentity(
				"diskCleanJob", "group1").build();

		// Trigger the job to run now, and then repeat every 5 minutes
		Trigger batchUpdateTrigger = newTrigger()
				.withIdentity("batchUpdateTrigger", "group1")
				.startNow()
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(ServiceConfig.getInt("scheduler.validation.dbpost.interval"))
								.repeatForever()).build();
		
		// Trigger the job to run now, and then repeat every 24 hours
		Trigger emailTrigger = newTrigger()
				.withIdentity("emailTrigger", "group1")
				.startNow()
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(ServiceConfig.getInt("scheduler.email.interval"))
								.repeatForever()).build();

		// Trigger the job to run now, and then repeat every 24 hours
		Trigger dcTrigger = newTrigger()
				.withIdentity("diskCleanTrigger", "group1")
				.startNow()
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(ServiceConfig.getInt("scheduler.diskclean.interval"))
								.repeatForever()).build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(batchUpdateJob, batchUpdateTrigger);
		scheduler.scheduleJob(emailJob, emailTrigger);
		scheduler.scheduleJob(dcJob, dcTrigger);

		// and start it off

		scheduler.start();
	}

	public static void main(String[] args) {
		HealthTrackScheduler scheduler = new HealthTrackScheduler();
		try {
			scheduler.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
