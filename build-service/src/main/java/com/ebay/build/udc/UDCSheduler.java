/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebay.build.udc;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ebay.build.utils.ServiceConfig;

/**
 *
 * @author wecai
 */
public class UDCSheduler {
	private static Logger logger = Logger.getLogger(UDCSheduler.class.getName());
    public void run() throws Exception {
    	logger.log(Level.INFO, "Running UDCSheduler...   ");
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail udcJob = newJob(UDCJob.class)
                .withIdentity("udcJob", "group3").build();

        Trigger trigger = newTrigger()
                .withIdentity("udcTrigger", "group3")
                .withSchedule(cronSchedule(ServiceConfig.get("scheduler.udc.time"))).build();

        scheduler.scheduleJob(udcJob, trigger);
        scheduler.start();
        logger.log(Level.INFO, "UDCSheduler started.");
    }
}
