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

import org.junit.Test;

import com.ccoe.build.core.model.Project;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.readers.ProcessHelper;

public class ProcessHelperTest {

	@Test
	public void parsePayload() {
		Session session = new Session();
		ProcessHelper.parseSessionPayLoad("git.url=https://github.scm.corp.ccoe.com/DevExTech/OSGI-Maven-plugins;machine=D-SHC-00436998;uname=mmao;maven.version=e:\\bin\\apache-maven-3.0.5-RaptorTimeTracking\\bin\\..;java.version=1.6.0_38-b05", 
				session);
		assertEquals("https://github.scm.corp.ccoe.com/DevExTech/OSGI-Maven-plugins", session.getGitUrl());
		assertEquals("mmao", session.getUserName());
		assertEquals("e:\\bin\\apache-maven-3.0.5-RaptorTimeTracking\\bin\\..", session.getMavenVersion());
		assertEquals("1.6.0_38-b05", session.getJavaVersion());
	}
	
	@Test
	public void parseProjectPlayload() {
		Project project = new Project();
		ProcessHelper.praseProjectPayload("com.ccoe.osgi.build:obrgen-maven-plugin:maven-plugin:1.1.0-SNAPSHOT", project);
		assertEquals("com.ccoe.osgi.build", project.getGroupId());
		assertEquals("obrgen-maven-plugin", project.getArtifactId());
		assertEquals("1.1.0-SNAPSHOT", project.getVersion());
		assertEquals("maven-plugin", project.getType());
	}
}
