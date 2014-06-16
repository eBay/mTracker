package com.ebay.build.service.udc;

import java.util.Date;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ebay.build.core.utils.DateUtils;
import com.ebay.build.udc.UDCUpdateJob;
import com.ebay.build.utils.SpringConfig;

@Path("")
public class UDCService {
	private static HashMap<String, UDCUpdateJob> jobs = new HashMap<String, UDCUpdateJob>();
	private static HashMap<String, Date> jobsStartTime = new HashMap<String, Date>();

	@POST
    @Path("/update/{appname}/start/{days}")
    @Produces(MediaType.TEXT_PLAIN)
	public String post(@HeaderParam("Content-Type") String contentType, 
			@PathParam("appname") String appName,
			@PathParam("days") int days,
            String body) {
		String returnMsg="Nothing to update";
		if(days<=0)
			days=30;
		Date date = DateUtils.addDays(DateUtils.getCurrDate(), -days);
		
		//appName is not ride and paypal
		if(!appName.equals("ride")&&!appName.equals("paypal")){
			return "The update job for " + appName + " is not implemented.";
		}

		if(jobs.containsKey(appName)&&checkJobExecuting(jobs.get(appName))){
			//appName is ride or paypal and there is a update job has been started and last update job is not finished.
			return "A update job for " + appName + " is already in process. ";
		}else{
			//appName is ride or paypal and there is no update job is executing.
			//create one and put it in jobs
			UDCUpdateJob job = (UDCUpdateJob)SpringConfig.getBean(appName+"UpdateJob");
			jobs.put(appName, job);
			job.setFromDate(date);
			job.start();
			jobsStartTime.put(appName, new Date());
		}
		
		returnMsg = "Update job for " + appName + " is starting...";
		return returnMsg;
	}
	@POST
    @Path("/update/{appname}/start")
    @Produces(MediaType.TEXT_PLAIN)
	public String post(@HeaderParam("Content-Type") String contentType, 
			@PathParam("appname") String appName,
			String body)
	{
		return post(contentType, appName, 30, body);
	}
	
	@GET
    @Path("/update/{appname}/query")
    @Produces(MediaType.TEXT_PLAIN)
	public String get(@HeaderParam("Content-Type") String contentType, 
			@PathParam("appname") String appName){
		String msg = "Last update job status: ";
		if(!appName.equals("ride")&&!appName.equals("paypal")){
			//appName is not ride and not paypal
			msg += "The update job for " + appName + " is not implemented.";
		} else if(!jobs.containsKey(appName)){
			//appName is ride or paypal, but there is no update job that has ever executed
			msg += "No update job has been executed.";
		} else{
			//there is update job for ride or paypal has ever executed.
			msg += "Start at "+jobsStartTime.get(appName)+"; ";
			msg += jobs.get(appName).getStatus();
		}
		return msg;
	}
	
	private boolean checkJobExecuting(UDCUpdateJob job){
		if(job!=null && job.getStatus().contains("Executing")){
			return true;
		}
		return false;
	}
	
}
