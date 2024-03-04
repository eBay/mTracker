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

package com.ccoe.build.core.readers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.ccoe.build.core.model.Phase;
import com.ccoe.build.core.model.Plugin;
import com.ccoe.build.core.model.Project;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.LineProcessor;

public class LineProcessorTest {
	
	LineProcessor processor = new LineProcessor();
	
	@Test
	public void testNewSession() {
		Session session = processor.newSession("0 SQLLog for CalTestParent-MavenBuild:D-SHC-00436998");
		
		assertEquals("CalTestParent", session.getAppName());
		assertEquals("D-SHC-00436998", session.getMachineName());
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
		assertTrue(processor.transactionStart("0  t17:41:38.58	Environment	RIDE", session));
		assertEquals("RIDE", session.getEnvironment());
	}
	
	@Test
	public void testTransactionEnd() {
		Session session = new Session();
		assertTrue(processor.transactionEnd("0  T17:41:55.83	Environment	RIDE	0	17248	git_url: null;machine=D-SHC-00436998;uname=mmao;maven.version=Apache Maven 3.0.5 (r01de14724cdef164cd33c7c8c2fe155faf9602da; 2013-02-19 21:51:28+0800);java.version=1.6.0_38-b05", session));
		
		assertEquals(17248, session.getDuration().longValue());
		assertEquals("0", session.getStatus());
		
		assertEquals("mmao", session.getUserName());
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
		String log = "2      T17:41:40.77	Project	Samples Parent	0	1134	com.ccoe.app.raptor:CalTestParent:pom:0.0.1-SNAPSHOT";
		assertTrue(processor.projectEnd(log, session));
		
		assertEquals("0", project.getStatus());
		assertEquals(1134, project.getDuration().longValue());
		assertEquals("com.ccoe.app.raptor", project.getGroupId());
		assertEquals("CalTestParent", project.getArtifactId());
		assertEquals("pom", project.getType());
		assertEquals("0.0.1-SNAPSHOT", project.getVersion());
		
		// pool not set to project
		// comment out per code refactoring, project needs pool?
		//assertNull(project.getPool());
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
		
		assertEquals(248, project.getLastPhase().getDuration().longValue());
		assertEquals("0", project.getLastPhase().getStatus());
		
		line = "3       T16:26:13.00  Phase validate 0 268 ";
		assertTrue(processor.phaseEnd(line, session));
		assertEquals(268L, project.getLastPhase().getDuration().longValue());
		assertEquals("0", project.getLastPhase().getStatus());
	}
	
	@Test
	public void testPluginAtom() {
		Session session = new Session();
		session.setStartTime(new Date());
		Project project = new Project();
		session.setCurrentProject(project);
		project.getPhases().add(new Phase());

		String line = "4          A17:41:39.74	Plugin	com.ccoe.osgi.build:dependency-version-validator:1.0.0	0	246	 (default)";
		assertTrue(processor.pluginAtom(line, session));
		
		Plugin plugin = session.getCurrentProject().getLastPhase().getPlugins().get(0);
		assertEquals("com.ccoe.osgi.build", plugin.getGroupId());
		assertEquals("dependency-version-validator", plugin.getArtifactId());
		assertEquals("1.0.0", plugin.getVersion());
		assertEquals("0", plugin.getStatus());
		assertEquals(246, plugin.getDuration().longValue());
		assertEquals("com.ccoe.osgi.build:dependency-version-validator:1.0.0", plugin.getPluginKey());
		
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
		Session session = pro.process(sb.toString());

		//System.out.println(sb.toString());
		
		assertEquals("RIDE", session.getEnvironment());
		assertEquals(3, session.getProjects().size());
		assertEquals(1134, session.getProjects().get("Samples Parent").getDuration().longValue());
		assertEquals(5086, session.getProjects().get("caltest").getDuration().longValue());
		assertEquals(9970, session.getProjects().get("EBA For caltest").getDuration().longValue());
		
		assertEquals(8, session.getProjects().get("Samples Parent").getPhases().size());
		
		Phase phase = session.getProjects().get("Samples Parent").getPhases().get(2);
		assertEquals("generate-resources", phase.getName());
		assertEquals(98, phase.getDuration().longValue());
		
		assertEquals(1, phase.getPlugins().size());
		
		assertEquals("com.ccoe.osgi.build", phase.getPlugins().get(0).getGroupId());
		assertEquals("maven-scm-build-info", phase.getPlugins().get(0).getArtifactId());
		assertEquals("1.0.7", phase.getPlugins().get(0).getVersion());
		assertEquals(98, phase.getPlugins().get(0).getDuration().longValue());
		assertEquals(Calendar.MARCH, phase.getPlugins().get(0).getStartTime().getMonth());
		assertEquals(1, phase.getPlugins().get(0).getStartTime().getDate());
		assertEquals(41, phase.getPlugins().get(0).getStartTime().getMinutes());
		assertEquals(40, phase.getPlugins().get(0).getStartTime().getSeconds());
	}
	
	@SuppressWarnings("resource")
	private Session getSessions(String fileName) throws IOException {
		BufferedReader br;
		br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(fileName).getFile()));
		
		String sCurrentLine = null;
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
			sb.append("\n");
		}
		
		LineProcessor pro = new LineProcessor();
		return pro.process(sb.toString());
	}
	
	@Test
	public void testExceptionSession() throws IOException {
		Session session = getSessions("exception.txt");
		
		assertEquals("CI", session.getEnvironment());
		assertEquals(2, session.getProjects().size());
		assertEquals("mmao", session.getUserName());
		assertTrue(session.getMavenVersion().equalsIgnoreCase("e:\\bin\\apache-maven-3.0.5-raptortimetracking\\bin\\.."));
		assertEquals(2066, session.getProjects().get("test3").getDuration().longValue());
		assertTrue(session.getExceptionMessage().contains("org.apache.maven.plugins:maven-compiler-plugin:2.5:compile "));
	}
	
	@Test
	public void testSkipLine() {
		assertTrue(processor.skipLine("Environment: raptor-build-tracking"));
		assertTrue(processor.skipLine("Label: unset;***;unset"));
		assertTrue(processor.skipLine("         -------------------------------------------------------"));
		assertFalse(processor.skipLine("0 SQLLog for CalTestParent-MavenBuild:D-SHC-00436998"));
	}
	
	@Test
	public void testSessionEnd() {
		Session session = new Session();
		String line = "1    T15:34:38.05	URL	Session	0	28771	[install]";
		assertTrue(processor.sessionEnd(line, session));
		assertEquals("install", session.getGoals());
		
		line = "1    T15:41:41.21	URL	Session	0	18959	[clean, install]";
		assertTrue(processor.sessionEnd(line, session));
		assertEquals("clean, install", session.getGoals());
	}
	
	@Test
	public void testProjectEnd() {
		Session session = new Session();
		processor.sessionStart("Start: 01-03-2013 17:41:38", session);
		assertTrue(processor.projectStart("2      t17:41:39.64	Project	mweb", session));

		// Test Project End
		String log = "2      T03:22:48.99	Project	mweb	log	72840	com.ccoe.app.raptor:mweb:war:1.0.0-SNAPSHOT";
		assertTrue(processor.projectEnd(log, session));
		
		Project project = session.getProjects().values().iterator().next();		
		assertEquals("com.ccoe.app.raptor", project.getGroupId());
		assertEquals("log", project.getStatus());
	}
}
