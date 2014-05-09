/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebay.build.udc;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.profiler.filter.FilterFactory;
import com.ebay.build.profiler.filter.PaypalFilterFactory;
import com.ebay.build.profiler.filter.RideFilterFactory;
import com.ebay.build.profiler.filter.model.Filter;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.utils.CompressUtils;
import com.ebay.build.utils.ServiceConfig;

/**
 * 
 * @author wecai
 */
public class UDCJob implements Job {

	 private final File QUEUE_DIR = new File(
             ServiceConfig.get("queue_root_dir"));

	 private final static Logger logger = Logger.getLogger(UDCJob.class.getName());

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
					new UsageDataRecorder(Arrays.asList(csvFiles), type).start();
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
	}
}