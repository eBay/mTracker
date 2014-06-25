package com.ccoe.build.persistent.healthcheck;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class HealthTrackDetailsJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public void create(final int trackId, final List<ErrorItem> items) {
		final String SQL = "insert into RBT_HEALTH_TRACK_DETAILS (track_id, project_name, category_name, severity, target, message, error_code) " +
				"values (?, ?, ?, ?, ?, ?, ?)";

		jdbcTemplateObject.batchUpdate(SQL,  new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ErrorItem error = items.get(i);
				ps.setInt(1, trackId);
				ps.setString(2, error.getProjectName());
				ps.setString(3, error.getCatgoryName());
				ps.setString(4, error.getSeverity());
				ps.setString(5, error.getTarget());
				ps.setString(6, error.getMessage());
				ps.setString(7, error.getErrorCode());
			}

			@Override
			public int getBatchSize() {
				return items.size();
			}
		});
	}
}
