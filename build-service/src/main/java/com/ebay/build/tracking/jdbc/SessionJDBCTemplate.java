package com.ebay.build.tracking.jdbc;


import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import com.ebay.build.profiler.model.Session;

public class SessionJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public List<Session> getSessionWithoutDod(String startDate, String endDate) {
		String SQL = "select * from RBT_SESSION " + " where status = 0 and duration_build is null and duration_download is null "				
				+ " and start_time > to_date('" + startDate + "', 'DD-Mon-YY HH24:Mi') "
				+ " and start_time < to_date('" + endDate + "', 'DD-Mon-YY HH24:Mi') ";

		return jdbcTemplateObject.query(SQL, new SessionMapper());
	}
	
	public void updateDownloadDuration(Session session, Long downloadDuration, Long buildDuration) {
		String SQL = "update RBT_SESSION set duration_download = ?, duration_build = ? where id = ?";
		jdbcTemplateObject.update(SQL, new Object[] {downloadDuration, buildDuration, session.getId()});		
	}
	
	public int[] batchUpdateDuration(final List<DurationObject> durations) {
		int[] updateCounts = jdbcTemplateObject.batchUpdate(
				"update RBT_SESSION set duration_download = ?, duration_build = ? where id = ?",
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setInt(1, (durations.get(i)).getDownloadDuration());
						ps.setInt(2, (durations.get(i)).getBuildDuration());
						ps.setInt(3, (durations.get(i)).getSessionId());
					}

					public int getBatchSize() {
						return durations.size();
					}
				});
		return updateCounts;
	}
}
