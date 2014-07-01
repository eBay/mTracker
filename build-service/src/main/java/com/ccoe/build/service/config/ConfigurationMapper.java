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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ConfigurationMapper implements RowMapper<Configuration> {

	@Override
	public Configuration mapRow(ResultSet rs, int arg1) throws SQLException {
		Configuration conf = new Configuration();
		
		if ("TRUE".equalsIgnoreCase(rs.getString("switch"))) {
			conf.setGlobalSwitch(true);
		} else {
			conf.setGlobalSwitch(false);
		}
		
		conf.setContacts(rs.getString("contacts"));
		conf.setSite(rs.getString("site"));
		
		return conf;
	}

}
