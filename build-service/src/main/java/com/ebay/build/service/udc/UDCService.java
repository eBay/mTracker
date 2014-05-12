package com.ebay.build.service.udc;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ebay.build.profiler.utils.DateUtils;
import com.ebay.build.udc.UDCUpdateJob;

@Path("")
public class UDCService {
	private static UDCUpdateJob rideJob = null;
	private static UDCUpdateJob paypalJob = null;
	private static Date rideJobStartDate = null;
	private static Date paypalJobStartDate = null;
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
		if(appName.equalsIgnoreCase("ride")){
			if(rideJob!=null && rideJob.getStatus().contains("Executing")){
				returnMsg = "A update job for Ride is already in process. ";
			}else{
				Date date = DateUtils.addDays(DateUtils.getCurrDate(), -days);
				rideJobStartDate = new Date();
				rideJob = new UDCUpdateJob(date, "");
				rideJob.start();
				returnMsg = "Update job for Ride is starting...";
			}
		}else if (appName.equalsIgnoreCase("paypal")){
			if(paypalJob!=null && paypalJob.getStatus().contains("Executing")){
				return "A update job for Paypal is already in process. ";
			}else{
				Date date = DateUtils.addDays(DateUtils.getCurrDate(), -days);
				paypalJobStartDate = new Date();
				paypalJob = new UDCUpdateJob(date, "paypal");
				paypalJob.start();
				returnMsg = "Update job for Paypal is starting...";
			}
		}else{}
		
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
		if(appName.equalsIgnoreCase("ride")&&rideJob!=null){
			msg += "(Start at "+rideJobStartDate+")";
			msg += rideJob.getStatus();
		}else if(appName.equalsIgnoreCase("paypal")&&paypalJob!=null){
			msg += "(Start at "+paypalJobStartDate+")";
			msg += paypalJob.getStatus();
		} else if(paypalJob==null && rideJob==null){
			msg += "No update job has been executed.";
		}else
		{
			msg +="Unknown App Name";
		}
		return msg;
	}
}
