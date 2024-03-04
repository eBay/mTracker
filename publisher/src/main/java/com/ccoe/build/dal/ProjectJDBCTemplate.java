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

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ccoe.build.core.model.Project;

public class ProjectJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public int create(final Project project, final String appName) {
		final String SQL = "insert into RBT_PROJECT (pool_name, name, group_id, artifact_id, type, version, " +
				"duration, status, start_time) " +
				"values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplateObject.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SQL,
						new String[] { "id" });
				ps.setString(1, appName);
				ps.setString(2, project.getName());
				ps.setString(3, project.getGroupId());
				ps.setString(4, project.getArtifactId());
				ps.setString(5, project.getType());
				ps.setString(6, project.getVersion());
				ps.setLong(7, project.getDuration());
				ps.setString(8, project.getStatus());
				ps.setTimestamp(9, new java.sql.Timestamp(project.getStartTime().getTime()));

				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
}
