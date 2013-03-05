package com.ebay.build.cal.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.ebay.build.cal.model.Project;
import com.ebay.build.cal.model.Session;

public class LineProcessorTest {
	
	LineProcessor processor = new LineProcessor();
	
	@Test
	public void testNewSession() {
		Session session = processor.newSession("SQLLog for CalTestParent-MavenBuild:D-SHC-00436998");
		
		assertEquals("CalTestParent", session.getPool().getName());
		assertEquals("D-SHC-00436998", session.getPool().getMachine().getName());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSessionStart() {
		Session session = new Session();
		assertTrue(processor.sessionStart("Start: 01-03-2013 17:41:38", session));
		assertEquals(17, session.getStartTime().getHours());
		assertEquals(41, session.getStartTime().getMinutes());
	}
	
	@Test
	public void testTransactionStart() {
		Session session = new Session();
		assertTrue(processor.transactionStart("0  t17:41:38.58	URL	RIDE", session));
		assertEquals("RIDE", session.getEnvironment());
	}
	
	@Test
	public void testTransactionEnd() {
		Session session = new Session();
		assertTrue(processor.transactionEnd("0  T17:41:55.83	URL	RIDE	0	17248	git_url: null;machine=D-SHC-00436998;uname=mmao;maven.version=Apache Maven 3.0.5 (r01de14724cdef164cd33c7c8c2fe155faf9602da; 2013-02-19 21:51:28+0800);java.version=1.6.0_38-b05", session));
		
		assertEquals(17248, session.getDuration());
		assertEquals("0", session.getStatus());
		// TODO: assert the payload
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testProjectStartEnd() {
		// prepare session
		Session session = new Session();
		processor.sessionStart("Start: 01-03-2013 17:41:38", session);
		
		
		assertTrue(processor.projectStart("2      t17:41:39.64	Project	Samples Parent", session));
		
		assertEquals("2-Samples Parent", session.getProjects().keySet().iterator().next());
		Project project = session.getProjects().values().iterator().next();
		assertEquals("Samples Parent", project.getName());
		
		Date startDate = project.getStartTime();
		assertEquals(Calendar.MARCH, startDate.getMonth());
		assertEquals(1, startDate.getDate());
		assertEquals(17, startDate.getHours());
		assertEquals(39, startDate.getSeconds());
		
		
		// Test Project End
		String log = "2      T17:41:40.77	Project	Samples Parent	0	1134	com.ebay.app.raptor:CalTestParent:pom:0.0.1-SNAPSHOT";
		assertTrue(processor.projectEnd(log, session));
		
		assertEquals("0", project.getStatus());
		assertEquals(1134, project.getDuration());
		//TODO assert the payload
	}
	
}
