package com.ccoe.build.persistent.healthcheck.scheduler;

import java.io.File;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccoe.build.core.utils.FileUtils;
import com.ccoe.build.utils.ServiceConfig;

public class DiskCleanJob  implements Job {
	
	public DiskCleanJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Executing disk clean...");
		String buildQueuePath = ServiceConfig.get("build_queue_dir");
		if (buildQueuePath == null) {
			System.out.println(buildQueuePath + " is not exists.");
		} else {
			FileUtils.diskClean(new File(buildQueuePath), 10);
		}
		
		String healthQueuePath = ServiceConfig.get("health_queue_dir");
		if (healthQueuePath == null) {
			System.out.println(healthQueuePath + " is not exists.");
		} else {
			FileUtils.diskClean(new File(healthQueuePath), 10);
		}
	}
}
