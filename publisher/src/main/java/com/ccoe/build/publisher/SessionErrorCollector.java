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

package com.ccoe.build.publisher;

import com.ccoe.build.core.model.Session;

public class SessionErrorCollector {
	private Session session;
	private String sessionID;
	private String errorMessage;
	
	public SessionErrorCollector(Session session) {
		this.session = session;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("------------ Session Error Collector --------------------\n");
		buffer.append("session id:" ).append(this.sessionID).append("\n");
		buffer.append("jenkins url:" ).append(this.session.getJenkinsUrl()).append("\n");
		buffer.append("git url:" ).append(this.session.getGitUrl()).append("\n");
		buffer.append("machine name: ").append(this.session.getMachineName()).append("\n");
		buffer.append("exception: " ).append(this.errorMessage).append("\n");
		buffer.append("--------------------------------------\n");
		
		return buffer.toString();
	}
	
}
