package com.ebay.build.reliability;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;


public class ReportInfoMapper implements RowMapper<ReportInfo>{

	
	public ReportInfo mapRow(ResultSet rs, int arg1) throws SQLException {
		
		ReportInfo reportInfo = new ReportInfo();		
		
		reportInfo.setSuccessfulSessions(rs.getInt("success"));
		reportInfo.setSystemErrors(rs.getInt("syserror"));
		reportInfo.setUserErrors(rs.getInt("usererror"));
		reportInfo.setUnknownErrors(rs.getInt("unknown"));
		reportInfo.setTotalSessions(rs.getInt("total") - rs.getInt("exclude"));
		
		return reportInfo;
	}
	
	
}
