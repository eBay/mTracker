/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ebay.build.udc;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.udc.dao.UsageDataDaoJDBCImpl;
import com.ebay.build.utils.CompressUtils;
import com.ebay.build.utils.ServiceConfig;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author wecai
 */
public class UDCJob implements Job {

	private final File QUEUE_DIR = new File(
			ServiceConfig.get("queue_root_dir"), "udc");
   

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        System.out.println("Executing UDC Job...");
        
        File csvDest = new File(QUEUE_DIR, "csv");
        if (!csvDest.exists()) {
            csvDest.mkdirs();
        }
        
        File wrongDest = new File(QUEUE_DIR, "wrong");
        if (!wrongDest.exists()) {
        	wrongDest.mkdirs();
        }

        File[] resultsFile = FileUtils.loadFiles(QUEUE_DIR, ".zip");
        if (resultsFile.length > 0) {
            for (File resultFile : resultsFile) {
                try {
                    CompressUtils.unCompress(resultFile, csvDest.getAbsolutePath());
                    org.apache.commons.io.FileUtils.deleteQuietly(resultFile);
                } catch (IOException ex) {
					Logger.getLogger(UDCJob.class.getName()).log(
							Level.SEVERE,
							"Cannot handle zip:" + resultFile + ", move to: "
									+ wrongDest, ex);
					try {
						org.apache.commons.io.FileUtils.moveFileToDirectory(
								resultFile, wrongDest, true);
					} catch (IOException e) {
					}
                    
                }
            }

        }


        File[] csvFiles = FileUtils.loadFiles(csvDest, ".csv");
        if (csvFiles.length > 0) {
            try {
                System.out.println("Processing files: " + ArrayUtils.toString(csvFiles));
                new UsageDataRecorder(Arrays.asList(csvFiles), new UsageDataDaoJDBCImpl()).start();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //FileUtils.renameDoneFile(resultFile);
            }
        } else {
            System.out.println("No csv records found under " + csvDest);
        }
    }
}