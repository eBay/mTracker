package com.ebay.build.cal.processors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.ebay.build.cal.model.Phase;
import com.ebay.build.cal.model.Plugin;
import com.ebay.build.cal.model.Project;
import com.ebay.build.cal.model.Session;

public class LineProcessorTest {
	
	LineProcessor processor = new LineProcessor();
	
	@Test
	public void testNewSession() {
		Session session = processor.newSession("0 SQLLog for CalTestParent-MavenBuild:D-SHC-00436998");
		
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
		
		assertEquals("Samples Parent", session.getProjects().keySet().iterator().next());
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
	
	@Test
	public void testPhase() {
		Session session = new Session();
		session.setStartTime(new Date());
		Project project = new Project();
		session.setCurrentProject(project);
		
		String line = "3        t17:41:39.74	Phase	validate";
		assertTrue(processor.phaseStart(line, session));
		
		assertEquals("validate", project.getPhases().get(0).getName());
		
		
		line = "3        T17:41:39.99	Phase	validate	0	248";
		assertTrue(processor.phaseEnd(line, session));
		
		assertEquals(248, project.getLastPhase().getDuration());
		assertEquals("0", project.getLastPhase().getStatus());
	}
	
	@Test
	public void testPluginAtom() {
		Session session = new Session();
		session.setStartTime(new Date());
		Project project = new Project();
		session.setCurrentProject(project);
		project.getPhases().add(new Phase());

		String line = "4          A17:41:39.74	Plugin	com.ebay.osgi.build:dependency-version-validator:1.0.0	0	246	 (default)";
		assertTrue(processor.pluginAtom(line, session));
		
		Plugin plugin = session.getCurrentProject().getLastPhase().getPlugins().get(0);
		assertEquals("com.ebay.osgi.build", plugin.getGroupID());
		assertEquals("dependency-version-validator", plugin.getArtifactID());
		assertEquals("1.0.0", plugin.getVersion());
		assertEquals("0", plugin.getStatus());
		assertEquals(246, plugin.getDuration());
		
		// TODO assert payload
	}
	
	@SuppressWarnings({ "resource", "deprecation" })
	@Test
	public void testSingleSessionProcess() throws IOException {
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

		//System.out.println(sb.toString());
		assertEquals(1, sessions.size());
		
		assertEquals("RIDE", sessions.get(0).getEnvironment());
		assertEquals(3, sessions.get(0).getProjects().size());
		assertEquals(1134, sessions.get(0).getProjects().get("Samples Parent").getDuration());
		assertEquals(5086, sessions.get(0).getProjects().get("caltest").getDuration());
		assertEquals(9970, sessions.get(0).getProjects().get("EBA For caltest").getDuration());
		
		assertEquals(8, sessions.get(0).getProjects().get("Samples Parent").getPhases().size());
		
		Phase phase = sessions.get(0).getProjects().get("Samples Parent").getPhases().get(2);
		assertEquals("generate-resources", phase.getName());
		assertEquals(98, phase.getDuration());
		
		assertEquals(1, phase.getPlugins().size());
		
		assertEquals("com.ebay.osgi.build", phase.getPlugins().get(0).getGroupID());
		assertEquals("maven-scm-build-info", phase.getPlugins().get(0).getArtifactID());
		assertEquals("1.0.7", phase.getPlugins().get(0).getVersion());
		assertEquals(98, phase.getPlugins().get(0).getDuration());
		assertEquals(Calendar.MARCH, phase.getPlugins().get(0).getEventTime().getMonth());
		assertEquals(1, phase.getPlugins().get(0).getEventTime().getDate());
		assertEquals(41, phase.getPlugins().get(0).getEventTime().getMinutes());
		assertEquals(40, phase.getPlugins().get(0).getEventTime().getSeconds());
	}
	
	
	
	@SuppressWarnings({ "resource", "deprecation" })
	@Test
	public void testTwoSessionsProcess() throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("two_sessions.txt").getFile()));
		
		String sCurrentLine = null;
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
			sb.append("\n");
		}
		
		LineProcessor pro = new LineProcessor();
		List<Session> sessions = pro.process(sb.toString());

		//System.out.println(sb.toString());
		assertEquals(2, sessions.size());
		
		assertEquals("RIDE", sessions.get(0).getEnvironment());
		assertEquals(3, sessions.get(0).getProjects().size());
		assertEquals(1526, sessions.get(0).getProjects().get("Samples Parent").getDuration());
		assertEquals(7693, sessions.get(0).getProjects().get("caltest").getDuration());
		assertEquals(19533, sessions.get(0).getProjects().get("EBA For caltest").getDuration());
		
		assertEquals("DEV", sessions.get(1).getEnvironment());
		assertEquals(3, sessions.get(1).getProjects().size());
		assertEquals(1169, sessions.get(1).getProjects().get("Samples Parent").getDuration());
		assertEquals(7188, sessions.get(1).getProjects().get("caltest").getDuration());
		assertEquals(10583, sessions.get(1).getProjects().get("EBA For caltest").getDuration());
		
		assertEquals(8, sessions.get(0).getProjects().get("Samples Parent").getPhases().size());
		
		Phase phase = sessions.get(1).getProjects().get("Samples Parent").getPhases().get(7);
		assertEquals("package", phase.getName());
		assertEquals(149, phase.getDuration());
		
		assertEquals(1, phase.getPlugins().size());
		
		assertEquals("org.apache.maven.plugins", phase.getPlugins().get(0).getGroupID());
		assertEquals("maven-source-plugin", phase.getPlugins().get(0).getArtifactID());
		assertEquals("2.1.2.ebay", phase.getPlugins().get(0).getVersion());
		assertEquals(149, phase.getPlugins().get(0).getDuration());
		assertEquals(Calendar.MARCH, phase.getPlugins().get(0).getEventTime().getMonth());
		assertEquals(6, phase.getPlugins().get(0).getEventTime().getDate());
		assertEquals(41, phase.getPlugins().get(0).getEventTime().getMinutes());
		assertEquals(23, phase.getPlugins().get(0).getEventTime().getSeconds());
	}
	
	@Test
	public void testSkipLine() {
		assertTrue(processor.skipLine("Environment: raptor-build-tracking"));
		assertTrue(processor.skipLine("Label: unset;***;unset"));
		assertTrue(processor.skipLine("         -------------------------------------------------------"));
		assertFalse(processor.skipLine("0 SQLLog for CalTestParent-MavenBuild:D-SHC-00436998"));
	}
}
