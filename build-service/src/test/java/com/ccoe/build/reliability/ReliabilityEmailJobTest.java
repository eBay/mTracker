package com.ccoe.build.reliability;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Date;

import org.junit.Test;

import com.ccoe.build.core.utils.DateUtils;
import com.ccoe.build.reliability.ReliabilityEmailJob;

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
		Date date = DateUtils.getOneWeekBack(new Date());
		Date midNight = DateUtils.getMidnightZero(date);
		System.out.println("[INFO]: Prepare getting reliability data!");
		double[] systemArray = {99.03, 99.33, 99.69, 98.71, 99.42, 99.93, 99.78};
		double[] overallArray = {95.34, 95.53, 94.96, 94.56, 95.94, 97.56, 94.88};
		System.out.println("[INFO]: Finish getting reliability data!");
		Object[] objectArrays = new Object[] {systemArray, overallArray};
		File embeddedImage = job.drawChart(file, midNight, objectArrays);
		assertNotNull(embeddedImage);
		assertTrue(embeddedImage.exists());
		assertEquals(embeddedImage.getAbsoluteFile().getParentFile().getParentFile(), file);
		assertTrue(embeddedImage.getAbsoluteFile().toString().endsWith("LineChart.jpeg"));
	}
	
}

