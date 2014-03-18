package com.ebay.build.alerts.devx;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ebay.build.utils.ServiceConfig;

public class DevxScheduler {
	
	public void run() throws Exception {
		Scheduler scheduler  = StdSchedulerFactory.getDefaultScheduler();
		JobDetail devxJob = newJob(DevxReportJob.class).withIdentity("devxJob", "group3").build();
		
		Trigger devxTrigger = newTrigger()
				.withIdentity("devxTrigger", "group3")
				.withSchedule(cronSchedule(ServiceConfig.get("scheduler.devx.time"))).build();
		
		scheduler.scheduleJob(devxJob, devxTrigger);
		scheduler.start();
	}

}
