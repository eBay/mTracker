package com.ebay.build.dal;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ebay.build.core.model.Session;

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
		session.setRaptorVersion(rs.getString("raptor_version"));
		session.setDomainVersion(rs.getString("domain_version"));
		session.setCategory(rs.getString("category"));
		
		session.setId(rs.getInt("id"));
		
		session.setFilter(rs.getString("filter"));
		
		return session;
	}
}
