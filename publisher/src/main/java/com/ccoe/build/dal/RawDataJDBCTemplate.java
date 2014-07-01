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

package com.ccoe.build.dal;

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

import com.ccoe.build.core.model.Plugin;

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
