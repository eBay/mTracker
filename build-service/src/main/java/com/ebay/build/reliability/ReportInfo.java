package com.ebay.build.reliability;

import java.text.DecimalFormat;

public class ReportInfo {
	private String systemReliabilityRate;
	private String overallReliabilityRate;

	// sessions count
	private int successfulSessions;
	private int failedSessions;
	private int totalSessions;

	// errors count
	private int systemErrors;
	private int userErrors;
	private int unknownErrors;

	public ReportInfo() {
		super();
	}

	public ReportInfo(int successfulSessions, int totalSessions,
			int systemErrors, int userErrors, int unknownErrors) {
		super();
		this.successfulSessions = successfulSessions;
		this.totalSessions = totalSessions;
		this.systemErrors = systemErrors;
		this.userErrors = userErrors;
		this.unknownErrors = unknownErrors;
	}

	// System Reliability = (1 -(System Errors + Unknown Errors) / Total
	// Sessions) * 100%
	public String getSystemReliabilityRate() {
		double beforeSetDecimal = (1 - (systemErrors + unknownErrors) * 1.0
				/ totalSessions) * 100;
		DecimalFormat df = new DecimalFormat("#0.00");
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		this.systemReliabilityRate = (df.format(beforeSetDecimal));
		return this.systemReliabilityRate;
	}

	// Overall Success Rate = Successful Sessions / Total Sessions * 100%
	public String getOverallReliabilityRate() {
		double beforeSetDecimal = successfulSessions * 1.0 / totalSessions
				* 100;
		DecimalFormat df = new DecimalFormat("#0.00");
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		this.overallReliabilityRate = (df.format(beforeSetDecimal));
		return this.overallReliabilityRate;
	}

	public int getSuccessfulSessions() {
		return successfulSessions;
	}

	public void setSuccessfulSessions(int successfulSessions) {
		this.successfulSessions = successfulSessions;
	}

	// Failed Sessions = System Errors + User Errors + Unknown Errors
	public int getFailedSessions() {
		this.failedSessions = this.systemErrors + this.userErrors
				+ this.unknownErrors;
		return this.failedSessions;
	}

	public int getTotalSessions() {
		return totalSessions;
	}

	public void setTotalSessions(int totalSessions) {
		this.totalSessions = totalSessions;
	}

	public int getSystemErrors() {
		return systemErrors;
	}

	public void setSystemErrors(int systemErrors) {
		this.systemErrors = systemErrors;
	}

	public int getUserErrors() {
		return userErrors;
	}

	public void setUserErrors(int userErrors) {
		this.userErrors = userErrors;
	}

	public int getUnknownErrors() {
		return unknownErrors;
	}

	public void setUnknownErrors(int unknownErrors) {
		this.unknownErrors = unknownErrors;
	}

}
