package com.ebay.build.reliability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

public class ReliabilityEmailJobTest {
	
	@Test
	public void testDrawChart() {
		ReliabilityEmailJob job = new ReliabilityEmailJob();
		File file = null;
		try {
			file = new File(ReliabilityEmailJob.class.getResource("/").toURI());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		assertNotNull(file);
		
		File embeddedImage = job.drawChart(file);
		assertNotNull(embeddedImage);
		assertTrue(embeddedImage.exists());
		assertEquals(embeddedImage.getAbsoluteFile().getParentFile().getParentFile(), file);
		assertTrue(embeddedImage.getAbsoluteFile().toString().endsWith("LineChart.jpeg"));
		embeddedImage.delete();
		assertFalse(embeddedImage.exists());
	}
	
}

