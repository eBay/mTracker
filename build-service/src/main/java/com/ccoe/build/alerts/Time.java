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

package com.ccoe.build.alerts;

public class Time {
	private String queryStart;
	private String queryEnd;
	private String send;
	public String getQueryStart() {
		return queryStart;
	}
	public void setQueryStart(String queryStart) {
		this.queryStart = queryStart;
	}
	public String getQueryEnd() {
		return queryEnd;
	}
	public void setQueryEnd(String queryEnd) {
		this.queryEnd = queryEnd;
	}
	public String getSend() {
		return send;
	}
	public void setSend(String send) {
		this.send = send;
	}
	
	
}
