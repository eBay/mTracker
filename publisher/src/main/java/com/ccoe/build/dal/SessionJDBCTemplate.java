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

//import oracle.jdbc.OraclePreparedStatement;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ccoe.build.core.model.Session;

public class SessionJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public int create(final Session session) {
		final String SQL = "insert into RBT_SESSION (pool_name, machine_name, user_name, environment, " +
				"status, duration, maven_version, goals, start_time, git_url, git_branch, jekins_url, java_version, cause, full_stacktrace, category, filter) " +
				"values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplateObject.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SQL,
						new String[] { "id" });
				ps.setString(1, session.getAppName());
				ps.setString(2, session.getMachineName());
				ps.setString(3, session.getUserName());
				ps.setString(4, session.getEnvironment());
				ps.setString(5, session.getStatus());
				ps.setLong(6, session.getDuration());
				ps.setString(7, session.getMavenVersion());
				ps.setString(8, session.getGoals());
				ps.setTimestamp(9, new java.sql.Timestamp(session.getStartTime().getTime()));
				ps.setString(10,  session.getGitUrl());
				ps.setString(11, session.getGitBranch());
				ps.setString(12, session.getJenkinsUrl());
				ps.setString(13, session.getJavaVersion());
				ps.setString(14, getCause(session.getExceptionMessage()));
				
				setFullStackTraceAsClob(15, ps, session);
				
				ps.setString(16,  session.getCategory());
				ps.setString(17,  session.getFilter());

				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	
	private void setFullStackTraceAsClob(int index, PreparedStatement ps, Session session) {
		String content = "";
		if (session.getFullStackTrace() != null) {
			content = session.getFullStackTrace();
		}
		
		//TODO: fix the set clob in case it is not Oracle DB, should work for other DB.
		// one of the reason here is we can not find the oracle jdbc jar in the public repo.

//		try {
//			((OraclePreparedStatement) ps).setStringForClob(index, content);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	private String getCause(String message) {
		if (message != null && message.length() > 800) {
			return message.substring(0, 750);
		}
		return message;
	}

	public Session getSession(Integer id) {
		String SQL = "select * from Session where id = ?";
		Session student = jdbcTemplateObject.queryForObject(SQL,
				new Object[] { id }, new SessionMapper());
		return student;
	}

	public List<Session> listSessions() {
		String SQL = "select * from Session";
		List<Session> students = jdbcTemplateObject.query(SQL,
				new SessionMapper());
		return students;
	}

	public void delete(Integer id) {
		String SQL = "delete from Session where id = ?";
		jdbcTemplateObject.update(SQL, id);
		System.out.println("Deleted Record with ID = " + id);
		return;
	}
}
