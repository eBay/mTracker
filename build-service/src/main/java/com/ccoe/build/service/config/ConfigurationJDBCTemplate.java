package com.ccoe.build.service.config;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class ConfigurationJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public Configuration getConfiguration(String appName) {
		String SQL = "select switch, contacts, site from rbt_build_service_config "
				+ "where app_name = ?";
		try {
			Configuration conf = jdbcTemplateObject.queryForObject(SQL,
					new Object[] { appName }, new ConfigurationMapper());
			return conf;
		} catch (EmptyResultDataAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}
