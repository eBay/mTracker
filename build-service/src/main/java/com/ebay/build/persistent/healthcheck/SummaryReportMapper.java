package com.ebay.build.persistent.healthcheck;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class SummaryReportMapper implements RowMapper<SummaryReport> {

	public SummaryReport mapRow(ResultSet rs, int arg1) throws SQLException {
		SummaryReport report = new SummaryReport();
		
		report.setJobName(rs.getString("job_name"));
		report.setBuildURL(rs.getString("build_url"));
		report.setGitBranch(rs.getString("git_branch"));
		report.setGitUrl(rs.getString("git_url"));
		report.setSeverity(rs.getString("severity"));
		report.setCount(rs.getInt("sumup"));
		
		return report;
	}
}

