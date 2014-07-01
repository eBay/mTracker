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

package com.ccoe.build.publisher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.publisher.LogPublisher;
import com.ccoe.build.publisher.PublisherConfig;

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
		
		File sessionFailFile = new File(targetFolder, "session_withexp.txt");
		session = publisher.process(sessionFailFile);
		assertNotNull(session);
		assertEquals("Test3Parent", session.getAppName());
		assertTrue(session.getExceptionMessage().contains("Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin"));
		assertTrue(session.getFullStackTrace().contains("... 19 more"));
	}
}
