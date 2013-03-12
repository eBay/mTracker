package com.ebay.build.cal.query.utils;

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

}
