package com.ebay.build.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BuildServiceSchedulerTest {
	@Test
	public void testIsSchedulerEnabled() {
		BuildServiceScheduler buildServiceScheduler = new BuildServiceScheduler();
		assertEquals(false, buildServiceScheduler.isSchedulerEnabled());
	}

}
