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

package com.ccoe.build.alerts.pfdash;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.CronScheduleBuilder.*;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import com.ccoe.build.utils.ServiceConfig;

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
