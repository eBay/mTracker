package com.ebay.build.profiler.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
	
	public static String getCALDateTimeString(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		return dateFormatter.format(date);
	}
	
	public static String getCALTimeString(Date date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.00");
		return dateFormatter.format(date);
	}
}
