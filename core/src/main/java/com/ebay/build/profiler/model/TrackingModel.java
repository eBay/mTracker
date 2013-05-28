package com.ebay.build.profiler.model;

import java.util.Date;

import com.ebay.build.profiler.utils.DateUtils;

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
