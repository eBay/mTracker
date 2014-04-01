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



	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		System.out.println("[INFO] " + new Date() + " Start executing UDCJob...");

		File[] files = QUEUE_DIR.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.equals("udc") || name.startsWith("udc_");
			}
		});

		for (File file : files) {
			System.out.println("Processing udc folder: " + file.getName());
			
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
						Logger.getLogger(UDCJob.class.getName()).log(Level.SEVERE,
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
					System.out.println("Processing files: " + ArrayUtils.toString(csvFiles));
					new UsageDataRecorder(Arrays.asList(csvFiles), new UsageDataDaoJDBCImpl(type)).start();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			} else {
				System.out.println("No csv records found under " + csvDest);
			}
			System.out.println("[INFO] End executing UDCJob...");
		}

	}
}