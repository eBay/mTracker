package com.ebay.build.persistent.healthcheck.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.utils.FileUtils;

public class DiskCleanJob  implements Job {
	
	public DiskCleanJob() {
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("Executing disk clean...");
		FileUtils.diskClean(FileUtils.QUEUE_DIR, 10);
	}
}
