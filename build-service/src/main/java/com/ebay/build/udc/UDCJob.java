/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebay.build.udc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.utils.CompressUtils;
import com.ebay.build.utils.ServiceConfig;
import com.ebay.build.utils.SpringConfig;

/**
 * 
 * @author wecai
 */
public class UDCJob implements Job {

	 private final File QUEUE_DIR = new File(
             ServiceConfig.get("queue_root_dir"));

	 private final static Logger logger = Logger.getLogger(UDCJob.class.getName());
	 private static boolean firstExecStatus = true;
	 
	 
	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		long startTime = System.currentTimeMillis();

		logger.log(Level.INFO, new Date() + " Start executing UDCJob...");
		
		File[] files = QUEUE_DIR.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.equals("udc") || name.startsWith("udc_");
			}
		});

		for (File file : files) {
			
			logger.log(Level.INFO, "Processing udc folder: " + file.getName());
			
			String type = StringUtils.substringAfter(file.getName(), "udc_");

			File udcQueue = new File(QUEUE_DIR, file.getName());
			File csvDest = new File(udcQueue, "csv");
			if (!csvDest.exists()) {
				csvDest.mkdirs();
			}

			File wrongDest = new File(udcQueue, "wrong");
			if (!wrongDest.exists()) {
				wrongDest.mkdirs();
			}

			// When UDCJob is not the first time to execute after server start
			// up, we should check if the last job has been finished.
			// check whether there are .csv files in udc or udc_**, if yes, that
			// means last job haven't be finished,
			// and this insert job will be cancelled.
			File[] beforeHandleCsvFiles = FileUtils.loadFiles(csvDest, ".csv");
			if (beforeHandleCsvFiles.length > 0 && !firstExecStatus) {
				String tempMsg;
				if (StringUtils.isEmpty(type) || type.trim().length() == 0) {
					tempMsg = "This insert job for ride is cancelled for last job is still executing.";
				} else {
					tempMsg = "This insert job for " + type
							+ " is cancelled for last job is still executing.";
				}
				logger.log(Level.SEVERE, tempMsg);
				continue;
			}

			File[] resultsFile = FileUtils.loadFiles(udcQueue, ".zip");
			if (resultsFile.length > 0) {
				for (File resultFile : resultsFile) {
					try {
						CompressUtils.unCompress(resultFile, csvDest.getAbsolutePath());
						org.apache.commons.io.FileUtils.deleteQuietly(resultFile);
					} catch (IOException ex) {
						logger.log(Level.SEVERE,
								"Cannot handle zip:" + resultFile + ", move to: " + wrongDest, ex);
						try {
							org.apache.commons.io.FileUtils.deleteQuietly(new File(wrongDest, resultFile.getName()));
							org.apache.commons.io.FileUtils.moveFileToDirectory(resultFile, wrongDest, true);
						} catch (IOException e) {
						}

					}
				}

			}

			File[] csvFiles = FileUtils.loadFiles(csvDest, ".csv");
			if (csvFiles.length > 0) {
				try {
					logger.log(Level.INFO,"Processing files: " + ArrayUtils.toString(csvFiles));
					String recorderName;
					if( StringUtils.isEmpty(type)){
						recorderName = "rideUsageDataRecorder"; 
					}else if(type.contains("paypal")){
						recorderName = "paypalUsageDataRecorder";
					} else{
						logger.log(Level.INFO, "Unknown type: " + type);
						continue;
					}
					UsageDataRecorder recorder = (UsageDataRecorder)SpringConfig.getBean(recorderName);
					recorder.setFiles(Arrays.asList(csvFiles));
					recorder.start();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} else {
				logger.log(Level.INFO, "No csv records found under " + csvDest);
			}
			logger.log(Level.INFO, "End executing UDCJob...");
		}
		long duration = System.currentTimeMillis() - startTime;
		logger.log(Level.INFO, "UDCJob execute time is " + duration);
		if(firstExecStatus){
			firstExecStatus=false;
		}
			
	}
}