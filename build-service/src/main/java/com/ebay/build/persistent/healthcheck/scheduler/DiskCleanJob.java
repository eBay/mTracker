package com.ebay.build.persistent.healthcheck.scheduler;

import java.io.File;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.utils.FileUtils;
import com.ebay.build.utils.ServiceConfig;

public class DiskCleanJob  implements Job {
	
	public DiskCleanJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Executing disk clean...");
		FileUtils.diskClean(new File(ServiceConfig.get("build_queue_dir")), 10);
		FileUtils.diskClean(new File(ServiceConfig.get("health_queue_dir")), 10);
	}
}
