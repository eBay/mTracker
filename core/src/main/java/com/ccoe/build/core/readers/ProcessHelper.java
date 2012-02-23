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

import com.ccoe.build.core.model.Project;
import com.ccoe.build.core.model.Session;
import com.ccoe.build.core.utils.StringUtils;

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
