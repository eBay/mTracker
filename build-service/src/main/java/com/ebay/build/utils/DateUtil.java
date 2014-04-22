package com.ebay.build.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	// Returns the date with the time set to 0:0:0
	public static Date getCurrDate() {
		Date date = new Date();
		date.setHours(0);
		date.setMinutes(0);
		date.setSeconds(0);
		return date;
	}
	public static Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, days);
		return cal.getTime();
	}
}
