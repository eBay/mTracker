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
