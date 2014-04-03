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

	public List<Session> getSessionWithoutDod(String claus) {
		return jdbcTemplateObject.query("select * from RBT_SESSION " + " where " + claus, 
				new SessionMapper());
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
	
	public List<Session> getExpSessionWithNullCategory() {
		String SQL = "select * from RBT_SESSION " +
				"where status = 1 and cause is not null " +
				" and (category is null or filter is null) " +
				" and start_time > sysdate - 10";
		
		return jdbcTemplateObject.query(SQL, new SessionMapper());
	}
	
	public void updateCategory(Session session) {
		String SQL = "update RBT_SESSION set category = ?,  filter = ? where id = ?";
		jdbcTemplateObject.update(SQL, new Object[] {session.getCategory(), session.getFilter(), session.getId()});
	}
	
	public int[] batchUpdateCategory(final List<Session> sessions) {
		int[] updateCounts = jdbcTemplateObject.batchUpdate(
				"update RBT_SESSION set category = ?,  filter = ? where id = ?",
				new BatchPreparedStatementSetter() {
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setString(1, sessions.get(i).getCategory());
						ps.setString(2, sessions.get(i).getFilter());
						ps.setInt(3, sessions.get(i).getId());
					}

					public int getBatchSize() {
						return sessions.size();
					}
				});
		return updateCounts;
	}
}
