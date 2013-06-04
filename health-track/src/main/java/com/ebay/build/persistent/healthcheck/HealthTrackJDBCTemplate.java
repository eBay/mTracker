package com.ebay.build.persistent.healthcheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ebay.build.validation.model.jaxb.Results;


public class HealthTrackJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public int create(final Results model) {
		final String SQL = "insert into RBT_HEALTH_TRACK (GIT_URL, GIT_BRANCH, GIT_COMMIT, BUILD_URL, LAST_MODIFIED_DATE) " +
				"values (?, ?, ?, ?, ?)";
		
		System.out.println(model.getGitURL() + " " + model.getGitBranch() + "  " + model.getGitCommit() + "  " + model.getBuildURL() + " " + model.getModifiedDate());

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplateObject.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SQL,
						new String[] { "id" });
				ps.setString(1, model.getGitURL());
				ps.setString(2, model.getGitBranch());
				ps.setString(3, model.getGitCommit());
				ps.setString(4, model.getBuildURL());
				if (model.getModifiedDate() != null) {
					ps.setTimestamp(5, new java.sql.Timestamp(model.getModifiedDate().getTime()));
				} else {
					ps.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
				}
				
				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	
	public boolean isExistIn24H(final String gitURL, final String gitBranch) {
		final String SQL = "select count(*) from RBT_HEALTH_TRACK where GIT_URL = ? and GIT_BRANCH = ? and last_modified_date > sysdate - 1";
		if (gitURL == null || gitBranch == null) {
			return false;
		}
		Integer count = (Integer) jdbcTemplateObject.queryForObject(
				SQL, new Object[] { gitURL, gitBranch }, Integer.class);
		if (count != null) {
			System.out.println(gitURL + "========== " + count + "   " + gitBranch);
			return count.intValue() > 0;
		} else {
			return true;
		}
	}
}
