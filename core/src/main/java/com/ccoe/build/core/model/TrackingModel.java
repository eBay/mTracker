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

package com.ccoe.build.core.model;

import java.util.Date;

import com.ccoe.build.core.utils.DateUtils;

public abstract class TrackingModel {
	private Date startTime;
	private Long duration = 0L;
	
	
	public String getAtomPrefix(int index, Date date) {
		return getPrefix(index, "A", date);
	}
	
	public String getTStartPrefix(int index, Date date) {
		return getPrefix(index, "t", date);
	}
	
	public String getTEndPrefix(int index, Date date) {
		return getPrefix(index, "T", date);
	}

	public String getPrefix(int index, String prefix, Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append(index).append(" ");
		for (int i = 0; i < index; i++) {
			sb.append("  ");
		}
		sb.append(prefix);
		sb.append(DateUtils.getCALTimeString(date));
		sb.append("  ");
		return sb.toString();
	}
	
	public void appendLine(StringBuffer sBuffer, String line) {
		sBuffer.append(line).append("\n");
	}
	
	public void appendTransacionStart(StringBuffer sBuffer, int index, String... parts) {
		sBuffer.append(getTStartPrefix(index, getStartTime()));
		for (String part : parts) {
			sBuffer.append(part).append(" ");
		}
		sBuffer.append("\n");
	}
	
	public void appendTransacionEnd(StringBuffer sBuffer, int index, String... parts) {
		sBuffer.append(getTEndPrefix(index, getStartTime()));
		for (String part : parts) {
			sBuffer.append(part).append(" ");
		}
		sBuffer.append("\n");
	}
	
	public void appendTransactionAtom(StringBuffer sBuffer, int index, String... parts) {
		sBuffer.append(getAtomPrefix(index, getStartTime()));
		for (String part : parts) {
			sBuffer.append(part).append(" ");
		}
		//sBuffer.append("\n");
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

}
