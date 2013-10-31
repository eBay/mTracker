package com.ebay.build.alerts.pfdash;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.alerts.AlertResult;
import com.ebay.build.alerts.Compare;
import com.ebay.build.alerts.Condition;
import com.ebay.build.alerts.SingleResult;
import com.ebay.build.alerts.Time;
import com.ebay.build.alerts.connector.Connector;
import com.ebay.build.profiler.utils.DateUtils;
import com.ebay.build.service.BuildServiceScheduler;
import com.ebay.build.utils.ServiceConfig;
import com.mongodb.DB;

public class PfDashJob implements Job {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		PfDashJob job = new PfDashJob();
		File file = null;
		try {
			file = new File(PfDashJob.class.getResource("/").toURI());
			job.run(file, "main");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		run(BuildServiceScheduler.contextPath, "execute");	
	}
	
	public void run(File file, String function) {
		String[] location = ServiceConfig.get("pfdash.db.host").split(";");
		List<String> locationList = Arrays.asList(location);
		int port = ServiceConfig.getInt("pfdash.db.port");
		String dbname = ServiceConfig.get("pfdash.db.name");
		DB db = Connector.connectDB(locationList, port, dbname);
		db.slaveOk();

		Date date = new Date();
		Date currentStartTime = DateUtils.getUTCOneDayBack(date);
		Date currentEndTime = DateUtils.getUTCMidnightZero(date);
		Condition current = new Condition();		
		current.setStartDate(currentStartTime);
		current.setEndDate(currentEndTime);
		
		Date previousStartTime = DateUtils.getUTCOneWeekBack(currentStartTime);
		Date previousEndTime = DateUtils.getUTCOneWeekBack(currentEndTime);
		Condition previous = new Condition();
		previous.setStartDate(previousStartTime);
		previous.setEndDate(previousEndTime);
		File parentDirectory = null;
		
		
		Time time = new Time();
		TimeZone timeZone = TimeZone.getDefault();
		TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
		time.setQueryStart(DateUtils.getDateTimeString(currentStartTime, utcTimeZone));
		time.setQueryEnd(DateUtils.getDateTimeString(currentEndTime, utcTimeZone));
		boolean sendFlag = false;
		try {
			parentDirectory = new File(PfDashJob.class.getResource("/").toURI());
			Compare com = new Compare(new File(parentDirectory,
					"alert_kpi_threshold.xml"), db);
			AlertResult ar = com.judgeRules(current, previous);
			time.setSend(DateUtils.getDateTimeString(date, timeZone));
			System.out.println("Email sentTime: " + date);
			EmailSender rlb = new EmailSender(ar, time);
			
			
			for (SingleResult singleResult : ar.getResultlist()) {
				if (!"#CACACA".equals(singleResult.getColor())) {
					sendFlag = true;
				}
			}
			if ("main".equals(function) || sendFlag && "execute".equals(function)) {			
				rlb.sendmail(file);
			} 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
}
