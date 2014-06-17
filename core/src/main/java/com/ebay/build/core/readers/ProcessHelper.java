package com.ebay.build.core.readers;

import com.ebay.build.core.model.Project;
import com.ebay.build.core.model.Session;
import com.ebay.build.core.utils.StringUtils;

public class ProcessHelper {
	
	public static void parseSessionPayLoad(String payload, Session session) {
		session.setPayload(payload);
		String[] properties = payload.split(";");
		for (String prop : properties) {
			String[] keyValue = prop.split("=");
			if ("uname".equals(keyValue[0])) {
				session.setUserName(keyValue[1]);
			}
			if ("maven.version".equals(keyValue[0])) {
				session.setMavenVersion(keyValue[1]);
			}
			if ("java.version".equals(keyValue[0])) {
				session.setJavaVersion(keyValue[1]);
			}
			if ("git.url".equals(keyValue[0])) {
				session.setGitUrl(keyValue[1]);
			}
			if ("jenkins.url".equals(keyValue[0])) {
				session.setJenkinsUrl(keyValue[1]);
			}
			if ("git.branch".equals(keyValue[0])) {
				session.setGitBranch(keyValue[1]);
			}
			if ("raptor.version".equals(keyValue[0])) {
				session.setRaptorVersion(keyValue[1]);
			}
			if ("domain.version".equals(keyValue[0])) {
				session.setDomainVersion(keyValue[1]);
			}
		}
	}
	
	public static void praseProjectPayload(String payload, Project project) {
		if (!StringUtils.isEmpty(payload)) {
			String[] gav = payload.split(":");
			if (gav != null) {
				if (gav.length == 4) {
					project.setGroupId(gav[0]);
					project.setArtifactId(gav[1]);
					project.setType(gav[2]);
					project.setVersion(gav[3]);
				}
			}
		}
	}
}
