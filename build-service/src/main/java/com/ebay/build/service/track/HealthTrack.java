package com.ebay.build.service.track;

import java.io.File;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ebay.build.utils.ServiceConfig;
import com.ebay.build.validation.model.jaxb.Results;
import com.ebay.build.validation.util.XmlProcessor;

@Path("/healthtrack")
public class HealthTrack {
	private final SimpleDateFormat actionDatePattern = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss-SSS");
	private final File queueRoot = new File(ServiceConfig.get("health_queue_dir"));
	
	public HealthTrack() {
		if (!queueRoot.exists()) {
			System.out.println("Making queue root: " + queueRoot);
			queueRoot.mkdirs();
		}
	}

    @POST
    @Consumes("application/xml")
    @Produces(MediaType.TEXT_PLAIN)
    public String post(Results results) {
    	if (results.getJobName() == null) {
    		return "ERROR: no job name provided.";
    	}
    	
    	if (results.getGitURL() == null) {
    		return "ERROR: cannot get the Git URL from the results.";
    	}
    			
    	String resultFileName = results.getJobName()
    			+ "_" + actionDatePattern.format(new Date()) + ".xml";
    	
    	XmlProcessor xmlProcessor = new XmlProcessor();
    	File resultFile = new File(queueRoot, resultFileName);
    	
    	System.out.println("queue the result file： " + resultFile.getAbsolutePath());
    	
    	xmlProcessor.marshal(resultFile, results);
    	return "request queued @ " + getHostName() + " on " + resultFile.getAbsolutePath();
    }
    
    private String getHostName() {
    	try {
			return java.net.InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}
    	return "";
    }
}