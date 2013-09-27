package com.ebay.build.alerts.pfdash;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebay.build.alerts.AlertResult;
import com.ebay.build.alerts.Compare;
import com.ebay.build.alerts.Condition;
import com.ebay.build.alerts.SingleResult;
import com.ebay.build.alerts.Time;
import com.ebay.build.alerts.connector.Connector;
import com.ebay.build.service.BuildServiceScheduler;
import com.ebay.build.utils.ServiceConfig;
import com.mongodb.DB;

public class PfDashJob implements Job {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String location = ServiceConfig.get("pfdash.db.host");
		int port = ServiceConfig.getInt("pfdash.db.port");
		String dbname = ServiceConfig.get("pfdash.db.name");
		DB db = Connector.connectDB(location, port, dbname);
		db.slaveOk();

		Date date = new Date();
		int seconds = getMilliSecondOfDay(date);
		Date currentStartTime = previousSeconds(date, seconds + 60 * 60 * 24);
		Date currentEndTime = previousSeconds(date, seconds);
		Condition current = new Condition();		
		current.setStartDate(currentStartTime);
		current.setEndDate(currentEndTime);
		
		Date previousStartTime = previousSeconds(date, seconds + 60 * 60 * 24 * 8);
		Date previousEndTime = previousSeconds(date, seconds + 60 * 60 * 24 * 7);
		Condition previous = new Condition();
		previous.setStartDate(previousStartTime);
		previous.setEndDate(previousEndTime);
		File parentDirectory = null;
		
		
		Time time = new Time();
		time.setQueryStart(dateFormatter(currentStartTime));
		time.setQueryEnd(dateFormatter(currentEndTime));
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		try {
			parentDirectory = new File(PfDashJob.class.getResource("/").toURI());
			Compare com = new Compare(new File(parentDirectory,
					"alert_kpi_threshold.xml"), db);
			AlertResult ar = com.judgeRules(current, previous);
			Date sendTime = new Date();
			time.setSend(sdf.format(sendTime));
			System.out.println("Email sentTime: " + sendTime);
			EmailSender rlb = new EmailSender(ar, time);
			rlb.sendmail(parentDirectory);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		String location = ServiceConfig.get("pfdash.db.host");
		int port = ServiceConfig.getInt("pfdash.db.port");
		String dbname = ServiceConfig.get("pfdash.db.name");
		DB db = Connector.connectDB(location, port, dbname);
		db.slaveOk();

		Date date = new Date();
		int seconds = getMilliSecondOfDay(date);
		Date currentStartTime = previousSeconds(date, seconds + 60 * 60 * 24);
		Date currentEndTime = previousSeconds(date, seconds);
		Condition current = new Condition();		
		current.setStartDate(currentStartTime);
		current.setEndDate(currentEndTime);
		
		Date previousStartTime = previousSeconds(date, seconds + 60 * 60 * 24 * 8);
		Date previousEndTime = previousSeconds(date, seconds + 60 * 60 * 24 * 7);
		Condition previous = new Condition();
		previous.setStartDate(previousStartTime);
		previous.setEndDate(previousEndTime);
		File parentDirectory = null;
		
		Time time = new Time();
		time.setQueryStart(dateFormatter(currentStartTime));
		time.setQueryEnd(dateFormatter(currentEndTime));
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		try {
			parentDirectory = new File(PfDashJob.class.getResource("/").toURI());
			Compare com = new Compare(new File(parentDirectory,
					"alert_kpi_threshold.xml"), db);
			AlertResult ar = com.judgeRules(current, previous);
			Date sendTime = new Date();
			time.setSend(sdf.format(sendTime));
			System.out.println("Email sentTime: " + sendTime);
			EmailSender rlb = new EmailSender(ar, time);
			
			boolean sendFlag = false;
			for (SingleResult singleResult : ar.getResultlist()) {
				if ("#FF9696".equals(singleResult.getThresholdColor()) 
						|| !"#CACACA".equals(singleResult.getThresholdDeltaColor())) {
					sendFlag = true;
				}
			}
			if (sendFlag) {			
				rlb.sendmail(BuildServiceScheduler.contextPath);
			} 
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static int getMilliSecondOfDay(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);
		return (hours * 60 * 60 + minutes * 60 + seconds);
	}
	
	public static Date previousSeconds(Date date, int seconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(calendar.SECOND, -seconds);
		date = calendar.getTime();
		return date;
	}
	
	public static String dateFormatter(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		return sdf.format(date);
	}
}
