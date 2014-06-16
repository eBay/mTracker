package com.ebay.build.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ebay.build.core.model.Plugin;

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
	
	public int[] batchInsert(final List<Plugin> plugins, final int sessionID, final int projectID) {
		final String SQL = "insert into RBT_RAW_DATA (plugin_id, session_id, " +
				"project_id, duration, event_time, plugin_key) values (?, ?, ?, ?, ?, ?)";
		
		int[] updateCounts = jdbcTemplateObject.batchUpdate(SQL,
						new BatchPreparedStatementSetter() {
							public void setValues(PreparedStatement ps, int i)
									throws SQLException {
								ps.setInt(1, plugins.get(i).getId());
								ps.setInt(2, sessionID);
								ps.setInt(3, projectID);
								ps.setLong(4, plugins.get(i).getDuration());
								ps.setTimestamp(5, new java.sql.Timestamp(plugins.get(i).getStartTime().getTime()));
								ps.setString(6, plugins.get(i).getGroupId() + ":" + plugins.get(i).getArtifactId());
							}

							public int getBatchSize() {
								return plugins.size();
							}
						});
		return updateCounts;
	}	
}
