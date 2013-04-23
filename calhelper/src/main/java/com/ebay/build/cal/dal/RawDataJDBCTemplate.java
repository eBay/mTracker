package com.ebay.build.cal.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ebay.build.cal.model.Plugin;

public class RawDataJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	// TODO event date
	public int create(final Plugin plugin, final int sessionID, final int projectID) {
		final String SQL = "insert into RBT_RAW_DATA (plugin_id, session_id, " +
				"project_id, duration, event_time, plugin_key) values (?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplateObject.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SQL,
						new String[] { "id" });
				ps.setInt(1, plugin.getId());
				ps.setInt(2, sessionID);
				ps.setInt(3, projectID);
				ps.setLong(4, plugin.getDuration());
				ps.setTimestamp(5, new java.sql.Timestamp(plugin.getStartTime().getTime()));
				ps.setString(6, plugin.getGroupId() + ":" + plugin.getArtifactId());

				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
}
