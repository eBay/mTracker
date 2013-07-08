package com.ebay.build.profiler.publisher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.ebay.build.profiler.model.Session;

public class LogPublisherTest {

	@Test
	public void testProcess() throws URISyntaxException {
		File targetFolder = new File(getClass().getResource("/").toURI());
		LogPublisher publisher = new LogPublisher(new PublisherConfig().targetFolder(targetFolder )
				.retensionDays(0));
		
		File sessionFile = new File(targetFolder, "single_session.txt");
		Session session = publisher.process(sessionFile);
		
		assertNotNull(session);
		assertEquals("CalTestParent", session.getAppName());
	}
}
