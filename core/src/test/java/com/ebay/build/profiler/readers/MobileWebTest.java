package com.ebay.build.profiler.readers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.readers.LineProcessor;

public class MobileWebTest {
	@SuppressWarnings("resource")
	@Test
	public void testProcess() throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("mobile_web_session.txt").getFile()));
		
		String sCurrentLine = null;
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
			sb.append("\n");
		}
		
		LineProcessor pro = new LineProcessor();
		List<Session> sessions = pro.process(sb.toString());
		assertEquals(1, sessions.size());
		
		assertEquals("MobileWeb", sessions.get(0).getPool().getName());
		
		assertEquals("MobileWeb", sessions.get(0).getProjects().get("Samples Parent").getPool().getName());
		
		assertNotNull(sessions.get(0).getProjects().get("mweb"));
		assertEquals("com.ebay.app.raptor", sessions.get(0).getProjects().get("mweb").getGroupId());
	}
}
