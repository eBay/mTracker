package com.ebay.build.service.config;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ebay.build.validation.model.Configuration;

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
