package com.ebay.build.profiler.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

public class StringUtilsTest {
	@Test
	public void testGetFirstFound() {
		assertNull(StringUtils.getFirstFound("", "", true));
	}
	
	@Test
	public void testGetFound() {
		assertEquals(0, StringUtils.getFound("", "", true).size());
		
		List<String> results = StringUtils.getFound("abc", "abc", true);
		assertEquals("abc", results.get(0));
	}
}