package com.ebay.build.cal.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;

import com.ebay.build.cal.model.Plugin;
import com.ebay.build.cal.model.Session;

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
		List<Session> sessions = pro.process(sb.toString());
		return sessions.get(0);
	}
	
	@Test
	public void testGetSessionAsString() throws IOException {
		assertTrue(getSession().toString().contains("4         A17:41:55.00  Plugin org.apache.maven.plugins:maven-install-plugin:2.4 0 60 (default-install) "));
	}
	
	@Test
	public void testPluginAsString() {
		Plugin plugin = new Plugin();
		plugin.setGroupId("com.ebay.raptor");
		plugin.setArtifactId("tracking");
		plugin.setVersion("1.0.0");
		plugin.setDuration(123L);
		plugin.setStatus("0");
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.MARCH, 13, 3, 0, 40);

		plugin.setStartTime(cal.getTime());
		
		assertEquals("4         A03:00:40.00  Plugin null 0 123 null ", plugin.toString());
	}
}
