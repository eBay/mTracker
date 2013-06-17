package com.ebay.build.persistent.healthcheck.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthTrackScheduler {

	public void run() throws Exception {
		Logger log = LoggerFactory.getLogger(HealthTrackScheduler.class);
		
        try {
            // Grab the Scheduler instance from the Factory 
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            // define the job and tie it to our BatchUpdateReportJob class
            JobDetail batchUpdateJob = newJob(BatchUpdateReportJob.class)
                .withIdentity("batchUpdateJob", "group1")
                .build();
            
            JobDetail emailJob = newJob(EmailSummaryPageJob.class)
                    .withIdentity("emailJob", "group1")
                    .build();
            
            JobDetail dcJob = newJob(DiskCleanJob.class)
                    .withIdentity("diskCleanJob", "group1")
                    .build();
            
            // Trigger the job to run now, and then repeat every 5 minutes
			Trigger batchUpdateTrigger = newTrigger()
					.withIdentity("batchUpdateTrigger", "group1")
					.startNow()
					.withSchedule(
							simpleSchedule().withIntervalInSeconds(60 * 5)
									.repeatForever()).build();
			
			Trigger emailTrigger = newTrigger()
					.withIdentity("emailTrigger", "group1")
					.startNow()
					.withSchedule(
							simpleSchedule().withIntervalInSeconds(60 * 60 * 24)
									.repeatForever()).build();
			
			Trigger dcTrigger = newTrigger()
					.withIdentity("diskCleanTrigger", "group1")
					.startNow()
					.withSchedule(
							simpleSchedule().withIntervalInSeconds(60 * 60 * 24)
									.repeatForever()).build();


            // Tell quartz to schedule the job using our trigger
            scheduler.scheduleJob(batchUpdateJob, batchUpdateTrigger);
            scheduler.scheduleJob(emailJob, emailTrigger);
            scheduler.scheduleJob(dcJob, dcTrigger);
            
            // and start it off
            scheduler.start();
            
            // wait long enough so that the scheduler as an opportunity to 
            // run the job!
            log.info("------- Waiting 65 seconds... -------------");
            try {
                // wait 65 seconds to show job
                Thread.sleep(65L * 1000L); 
                // executing...
            } catch (Exception e) {
            	e.printStackTrace();
            }

            scheduler.shutdown();

        } catch (SchedulerException se) {
            se.printStackTrace();
        }
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
