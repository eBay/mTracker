package com.ebay.build.reliability;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ebay.build.utils.ServiceConfig;

public class ReliabilityEmailScheduler {
	
	public void run() throws Exception {
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobDetail ciEmailJob = newJob(ReliabilityEmailJob.class)
				.withIdentity("CIBuildReliabilityEmailJob", "group1").build();
//		JobDetail ideEmailJob = newJob(SpaceReliabilityEmailJob.class)
//				.withIdentity("IDEBuildReliabilityEmailJob", "group1").build();
		
		String startTime = ServiceConfig.get("scheduler.reliability.email.starttime");
		Date date = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss", Locale.US).parse(startTime);
		
		Trigger ciEmailTrigger = newTrigger()
				.withIdentity("ciEmailTrigger", "group1")
				.startAt(date)
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(ServiceConfig.getInt("scheduler.reliability.email.internal"))
						.repeatForever()).build();
		
//		Trigger ideEmailTrigger = newTrigger()
//				.withIdentity("ideEmailTrigger", "group1")
//				.startAt(date)
//				.withSchedule(
//						simpleSchedule().withIntervalInSeconds(ServiceConfig.getInt("scheduler.reliability.email.internal"))
//						.repeatForever()).build();
		
		scheduler.scheduleJob(ciEmailJob, ciEmailTrigger);
//		scheduler.scheduleJob(ideEmailJob, ideEmailTrigger);
		
		scheduler.start();
	}
	
	public static void main(String[] args) {
		ReliabilityEmailScheduler scheduler = new ReliabilityEmailScheduler();
		try {
			scheduler.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
