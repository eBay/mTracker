package com.ebay.build.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ebay.build.cal.model.Pool;
import com.ebay.build.cal.model.Project;

public class ProjectMapper implements RowMapper<Project> {

	public Project mapRow(ResultSet rs, int arg1) throws SQLException {
		Project project = new Project();
		Pool pool = new Pool();
		pool.setName(rs.getString("pool_name"));
		project.setPool(pool);
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
