package com.ebay.build.profiler.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
	public static Calendar getAnHourBack() {
		return getAnHourBack(new Date());
	}
	
	public static Calendar getAnHourBack(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.HOUR_OF_DAY, -1);
		
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
	public static Date getMidnightZero(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	public static Date getUTCMidnightZero(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT-0"));
		cal.setTime(d);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	public static Date getOneDayBack(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	public static Date getUTCOneDayBack(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT-0"));
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	public static Date getOneWeekBack(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
	
	public static Date getUTCOneWeekBack(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("GMT-0"));
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_MONTH, -7);
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}
		
	public static String getCALDateTimeString(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormatter.format(date);
	}
	
	public static String getDateTimeString(Date date, TimeZone timeZone) {	
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
		dateFormatter.setTimeZone(timeZone);
		return dateFormatter.format(date);
	}
	
	public static String getCALTimeString(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.00");
		return dateFormatter.format(date);
	}
}
