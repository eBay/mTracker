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
