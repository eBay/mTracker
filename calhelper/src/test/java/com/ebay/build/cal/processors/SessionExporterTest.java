package com.ebay.build.cal.processors;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
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
	
	//@Test
	public void testGetSessionAsString() throws IOException {
		SessionExporter exporter = new SessionExporter();
		
		String modelString = exporter.getModelAsString(getSession());
		System.out.println(modelString);
	}
	
	@Test
	public void testPluginAsString() {
		Plugin plugin = new Plugin();
		plugin.setGroupId("com.ebay.raptor");
		plugin.setArtifactId("tracking");
		plugin.setVersion("1.0.0");
		plugin.setDuration(123L);
		plugin.setStatus("0");
		plugin.setStartTime(new Date());
		
		System.out.println("====");
		System.out.println(plugin.toString());
		System.out.println("====");
	}
}
