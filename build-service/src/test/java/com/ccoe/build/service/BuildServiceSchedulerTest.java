package com.ccoe.build.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ccoe.build.service.BuildServiceScheduler;

public class BuildServiceSchedulerTest {
	@Test
	public void testIsSchedulerEnabled() {
		BuildServiceScheduler buildServiceScheduler = new BuildServiceScheduler();
		assertEquals(false, buildServiceScheduler.isSchedulerEnabled());
	}

}
