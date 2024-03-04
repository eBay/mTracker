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

package com.ccoe.build.core.writers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.ccoe.build.core.model.Plugin;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.LineProcessor;
import com.ccoe.build.core.writers.SessionExporter;

public class SessionExporterTest {
	
	@SuppressWarnings("resource")
	private Session getSession() throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("single_session.txt").getFile()));
		
		String sCurrentLine = null;
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
			sb.append("\n");
		}
		
		LineProcessor pro = new LineProcessor();
		Session session = pro.process(sb.toString());
		return session;
	}
	
	@Test
	public void testGetSessionAsString() throws IOException {
		assertTrue(getSession().toString().contains("4         A17:41:55.00  Plugin org.apache.maven.plugins:maven-install-plugin:2.4 0 60 (default-install) "));
	}
	
	@Test
	public void testPluginAsString() {
		Plugin plugin = new Plugin();
		plugin.setGroupId("com.ccoe.raptor");
		plugin.setArtifactId("tracking");
		plugin.setVersion("1.0.0");
		plugin.setDuration(123L);
		plugin.setStatus("0");
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.MARCH, 13, 3, 0, 40);

		plugin.setStartTime(cal.getTime());
		
		assertEquals("4         A03:00:40.00  Plugin null 0 123 null ", plugin.toString());
	}
	
	@Test
	public void testProcess() throws URISyntaxException {
		File targetFolder = new File(getClass().getResource(".").toURI());
		System.out.println(targetFolder + "-===================");

		SessionExporter exporter = new SessionExporter();
		Session session = new Session();
		session.setAppName("test");
		session.setMachineName("localhost");
		session.setStartTime(new Date());
		List<File> files = exporter.process(session, targetFolder);
		assertEquals(1, files.size());
		assertTrue(files.iterator().next().exists());
		
		session.setFullStackTrace("test stacktrace");
		files = exporter.process(session, targetFolder);
		assertEquals(2, files.size());
		
		File sessionFile = files.get(0);
		System.out.println(sessionFile + "-===================");
		assertTrue(sessionFile.getName().startsWith("test--localhost--"));
		assertTrue(sessionFile.exists());
		
		File stackTraceFile = files.get(1);
		assertTrue(stackTraceFile.getName().endsWith(".log.stacktrace"));
		assertTrue(stackTraceFile.exists());
	}
}
