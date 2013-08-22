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
	
	public void run() throws Exception{
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		
		JobDetail sendMailJob = newJob(ReliabilityEmailJob.class)
				.withIdentity("sendMailJob", "group1").build();
		
		String string = ServiceConfig.get("scheduler.reliability.email.starttime");
		Date date = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss", Locale.US).parse(string);
		
		Trigger sendMailTrigger = newTrigger()
				.withIdentity("sendMailTrigger", "group1")
				.startAt(date)
				.withSchedule(
						simpleSchedule().withIntervalInSeconds(ServiceConfig.getInt("scheduler.reliability.email.internal"))
						.repeatForever()).build();
		
		scheduler.scheduleJob(sendMailJob, sendMailTrigger);
		
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
