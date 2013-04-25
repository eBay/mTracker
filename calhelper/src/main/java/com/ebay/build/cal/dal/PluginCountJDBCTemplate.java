package com.ebay.build.cal.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class PluginCountJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public int create(final String pluginKey) {
		final String SQL = "insert into RBT_PLUGIN_COUNT_IN (plugin_key, last_modified_date) values (?, sysdate)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplateObject.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SQL, new String[]{"id"});
				ps.setString(1, pluginKey.trim());
		
				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
}

