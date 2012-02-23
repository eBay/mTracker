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

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ccoe.build.core.model.Session;

public class SessionMapper implements RowMapper<Session> {

	public Session mapRow(ResultSet rs, int arg1) throws SQLException {
		Session session = new Session();
		session.setDuration(rs.getLong("duration"));
		session.setEnvironment(rs.getString("environment"));
		session.setJavaVersion(rs.getString("java_version"));
		session.setMavenVersion(rs.getString("maven_version"));
		session.setStartTime(rs.getDate("start_time"));
		session.setUserName(rs.getString("user_name"));
		session.setStatus(rs.getString("status"));
		session.setGoals(rs.getString("goals"));
		session.setAppName(rs.getString("pool_name"));
		session.setMachineName(rs.getString("machine_name"));
		
		Clob stacktrace = rs.getClob("full_stacktrace");
		if (stacktrace != null) {
			session.setFullStackTrace(stacktrace.getSubString(1,  (int) stacktrace.length()));
		}
		
		session.setExceptionMessage(rs.getString("cause"));
		session.setCategory(rs.getString("category"));
		
		session.setId(rs.getInt("id"));
		
		session.setFilter(rs.getString("filter"));
		
		return session;
	}
}
