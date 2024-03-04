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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import org.junit.Test;

import com.ccoe.build.core.model.Phase;
import com.ccoe.build.core.model.Plugin;
import com.ccoe.build.core.model.Project;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.JsonProcessor;

public class JsonProcessorTest {
	
	@Test
	public void testProcessDefault() throws Exception {
		JsonProcessor processor = new JsonProcessor();
		String content = readTestResource("maven-time-tracking_06-26-2013_14-04-23-096.json");
		Session session = processor.process(content);
		assertNotNull(session);
		
		assertEquals("maven-time-tracking", session.getAppName());
		assertEquals("D-SHC-00436998", session.getMachineName());
		assertEquals(1, session.getProjects().size());
		Map<String, Project> projects = session.getProjects();
		assertEquals((Long)3814L, session.getDuration());
		
		Project project = projects.values().iterator().next();
		assertEquals("Build Profiler Core", project.getName());
		assertEquals(8, project.getPhases().size());
		
		Phase cleanPhase = project.getPhases().get(0);
		assertEquals(667, cleanPhase.getDuration().longValue());
		Plugin cleanPlugin = cleanPhase.getPlugins().iterator().next();
		assertEquals("org.apache.maven.plugins", cleanPlugin.getGroupId());
		assertEquals("maven-clean-plugin", cleanPlugin.getArtifactId());
		assertEquals(665, cleanPlugin.getDuration().longValue());
	}
	
	
	@Test
	public void testProcessException() throws Exception {
		JsonProcessor processor = new JsonProcessor();
		String content = readTestResource("test-exception.json");
		Session session = processor.process(content);
		assertNotNull(session);
		
		assertEquals("maven-time-tracking", session.getAppName());
		assertEquals("D-SHC-00436998", session.getMachineName());
		assertEquals(1, session.getProjects().size());
		Map<String, Project> projects = session.getProjects();
		assertEquals((Long)4815L, session.getDuration());
		
		assertEquals("Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.10:test (default-test) on project core: There are test failures.\n\nPlease refer to E:\\src\\RaptorBuildTracking\\maven-time-tracking\\core\\target\\surefire-reports for the individual test results.", session.getExceptionMessage());
		assertTrue(session.getFullStackTrace().contains("org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.10:test"));
		
		Project project = projects.values().iterator().next();
		assertEquals("Build Profiler Core", project.getName());
		assertEquals(6, project.getPhases().size());
		
		Phase cleanPhase = project.getPhases().get(0);
		assertEquals(795, cleanPhase.getDuration().longValue());
		Plugin cleanPlugin = cleanPhase.getPlugins().iterator().next();
		assertEquals("org.apache.maven.plugins", cleanPlugin.getGroupId());
		assertEquals("maven-clean-plugin", cleanPlugin.getArtifactId());
		assertEquals(793, cleanPlugin.getDuration().longValue());
	}
	
	@SuppressWarnings("resource")
	private String readTestResource(String fileName) throws Exception {
		BufferedReader br;
		br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource(fileName).getFile()));
		
		String sCurrentLine = null;
		StringBuffer sb = new StringBuffer();
		while ((sCurrentLine = br.readLine()) != null) {
			sb.append(sCurrentLine);
			sb.append("\n");
		}
		return sb.toString();
	}
}
