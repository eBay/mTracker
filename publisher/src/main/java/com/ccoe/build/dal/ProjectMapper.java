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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ccoe.build.core.model.Project;

public class ProjectMapper implements RowMapper<Project> {

	public Project mapRow(ResultSet rs, int arg1) throws SQLException {
		Project project = new Project();
		project.setName(rs.getString("name"));
		project.setGroupId(rs.getString("group_id"));
		project.setArtifactId(rs.getString("artifact_id"));
		project.setType(rs.getString("type"));
		project.setVersion(rs.getString("version"));
		project.setStartTime(rs.getDate("start_time"));
		project.setDuration(rs.getLong("duration"));
		project.setStatus(rs.getString("status"));
		return project;
	}
}
