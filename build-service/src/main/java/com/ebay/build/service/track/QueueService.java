package com.ebay.build.service.track;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;

import com.ebay.build.utils.ServiceConfig;

@Path("")
public class QueueService {
	private final SimpleDateFormat actionDatePattern = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss-SSS");
	private final File queueRoot = new File(ServiceConfig.get("queue_root_dir"));
	
	public QueueService() {
		if (!queueRoot.exists()) {
			System.out.println("Making queue root: " + queueRoot);
			queueRoot.mkdirs();
		}
	}

	@POST @Path("/queue/{appname}/{jobname}")
    @Produces(MediaType.TEXT_PLAIN)
    public String post(@HeaderParam("Content-Type") String contentType, @PathParam("appname") String appName, 
    		@PathParam("jobname") String jobName, 
    		String body) {
		File appQueueFolder = new File(queueRoot, appName);
		if (!appQueueFolder.exists()) {
			System.out.println("Making application queue root: " + appQueueFolder);
			appQueueFolder.mkdirs();
		}
		String extension = ".txt";
		if (contentType != null && contentType.contains("/")) {
			extension = "." + contentType.split("/")[1];
		}
		String resultFileName = jobName + "_" + actionDatePattern.format(new Date()) + extension;
		File resultFile = new File(appQueueFolder, resultFileName);
		
		try {
			FileUtils.writeStringToFile(resultFile, body);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return appName + "/" + jobName + " queued @ " + getHostName() + " on folder: " + resultFile;
    }
    
    private String getHostName() {
    	try {
			return java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
    	return "";
    }	
}
