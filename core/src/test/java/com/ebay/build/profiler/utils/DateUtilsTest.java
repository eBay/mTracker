package com.ebay.build.profiler.utils;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

public class DateUtilsTest {
	@Test
	public void testGetCALDateTimeString() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2011);
		cal.set(Calendar.MONTH, Calendar.MAY);
		cal.set(Calendar.DAY_OF_MONTH, 8);
		
		cal.set(Calendar.HOUR, 17);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		assertEquals("09-05-2011 05:00:00", DateUtils.getCALDateTimeString(cal.getTime()));
		
		assertEquals("05:00:00.00", DateUtils.getCALTimeString(cal.getTime()));
		
		assertEquals("09-05-2011 04:00:00", 
				DateUtils.getCALDateTimeString(DateUtils.getAnHourBack(cal.getTime()).getTime()));
	}
}
