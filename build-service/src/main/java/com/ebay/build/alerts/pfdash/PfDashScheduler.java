package com.ebay.build.alerts.pfdash;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.*;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ebay.build.utils.ServiceConfig;

public class PfDashScheduler {
	public void run() throws Exception {
		Scheduler scheduler  = StdSchedulerFactory.getDefaultScheduler();
		JobDetail pfDashJob = newJob(PfDashJob.class)
				.withIdentity("pfDashJob", "group3").build();
		
		Trigger pdDashTrigger = newTrigger()
				.withIdentity("pdDashTrigger", "group3")
				.withSchedule(cronSchedule(ServiceConfig.get("scheduler.pfdash.time"))).build();
		
		scheduler.scheduleJob(pfDashJob, pdDashTrigger);
		scheduler.start();
	}
}
