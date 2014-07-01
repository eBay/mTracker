/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.core.utils;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import com.ccoe.build.core.utils.DateUtils;


public class DateUtilsTest {
	@Test
	public void testGetCALDateTimeString() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, 2011);
		cal.set(Calendar.MONTH, Calendar.MAY);
		cal.set(Calendar.DAY_OF_MONTH, 8);
		
		cal.set(Calendar.HOUR_OF_DAY, 17);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		
		assertEquals("08-05-2011 17:00:00", DateUtils.getCALDateTimeString(cal.getTime()));
		
		assertEquals("17:00:00.00", DateUtils.getCALTimeString(cal.getTime()));
		
		assertEquals("08-05-2011 16:00:00", 
				DateUtils.getCALDateTimeString(DateUtils.getAnHourBack(cal.getTime()).getTime()));
	}

}
