/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebay.build.udc;

import com.ebay.build.utils.ServiceConfig;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author wecai
 */
public class UDCSheduler {

    public void run() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail udcJob = newJob(UDCJob.class)
                .withIdentity("udcJob", "group3").build();

        Trigger trigger = newTrigger()
                .withIdentity("udcTrigger", "group3")
                .withSchedule(cronSchedule(ServiceConfig.get("scheduler.udc.time"))).build();

        scheduler.scheduleJob(udcJob, trigger);
        scheduler.start();
    }
}
