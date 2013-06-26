package com.ebay.build.persistent.healthcheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
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
		final String SQL = "insert into RBT_HEALTH_TRACK (GIT_URL, GIT_BRANCH, GIT_COMMIT, BUILD_URL, LAST_MODIFIED_DATE, JOB_NAME) " +
				"values (?, ?, ?, ?, ?, ?)";
		
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
				ps.setString(6, model.getJobName());
				
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
	
	public List<SummaryReport> getSummaryReport() {
		String SQL = "select a.job_name, a.git_url, a.git_branch, a.build_url, b.severity, count(*) as sumup  from rbt_health_track a, rbt_health_track_details b "
				+ "where a.id = b.track_id  and last_modified_date > sysdate - 15 group by a.job_name, a.git_url, a. git_branch, a.build_url, b.severity";
		try {
			List<SummaryReport> report = jdbcTemplateObject.query(SQL,
					new Object[] { }, new SummaryReportMapper());
			return report;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
