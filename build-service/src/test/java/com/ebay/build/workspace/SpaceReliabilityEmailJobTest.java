package com.ebay.build.workspace;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.ebay.build.reliability.ReliabilityEmailJob;

public class SpaceReliabilityEmailJobTest {
		
	@Test
	public void testDrawChart() {
		SpaceReliabilityEmailJob job = new SpaceReliabilityEmailJob();
		File file = null;
		try {
			file = new File(ReliabilityEmailJob.class.getResource("/").toURI());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
		File[] embeddedImages = job.drawChart(file);
		assertNotNull(embeddedImages);
		for(File embeddedImage : embeddedImages) {			
			embeddedImage.deleteOnExit();
		}
	}
}
