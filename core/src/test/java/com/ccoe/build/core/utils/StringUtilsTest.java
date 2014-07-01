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
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.ccoe.build.core.utils.StringUtils;

public class StringUtilsTest {
	@Test
	public void testGetFirstFound() {
		assertNull(StringUtils.getFirstFound("", "", true));
		
		assertEquals("https://", StringUtils.getFirstFound("https://abc.com", "(https://)", true));
		assertEquals("http://", StringUtils.getFirstFound("http://abc.com", "(https?://)", true));
	}
	
	@Test
	public void testGetFound() {
		assertEquals(0, StringUtils.getFound("", "", true).size());
		
		List<String> results = StringUtils.getFound("abc", "abc", true);
		assertEquals("abc", results.get(0));
	}
	
	@Test
	public void testCalendar() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		System.out.println(c.getTime());
		
		c.add(Calendar.DAY_OF_MONTH, -1);
		
		System.out.println(c.getTime());
	}
}
