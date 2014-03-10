package com.ebay.build.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
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
	
	public List<String> getIncludedPlugins() {
		String SQL = "select plugin_key from rbt_plugin_count_in";
		List<String> resutls = new ArrayList<String>();
		try {
			List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(SQL);
			Iterator<Map<String, Object>> it = rows.iterator();  
			while(it.hasNext()) {  
			    Map<String, Object> userMap = (Map<String, Object>) it.next(); 
			    resutls.add(((String) userMap.get("plugin_key")).trim());  
			}  
			return resutls;
		} catch (EmptyResultDataAccessException e) {
			// empty
		}
		return resutls;
	}
	
	public List<String> getRaptor2CIMachines() {
		String SQL = "select machine_name from rbt_session where raptor_version like '2.0.0%' group by machine_name";
		List<String> resutls = new ArrayList<String>();
		try {
			List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(SQL);
			Iterator<Map<String, Object>> it = rows.iterator();  
			while(it.hasNext()) {  
			    Map<String, Object> userMap = (Map<String, Object>) it.next();
			    String machineName = ((String) userMap.get("machine_name")).trim();
			    if (!machineName.endsWith(".stratus.phx.qa.ebay.com")) {
			    	machineName += ".stratus.phx.qa.ebay.com";
			    }
			    resutls.add(machineName);  
			}  
			return resutls;
		} catch (EmptyResultDataAccessException e) {
			// empty
		}
		return resutls;
	}

	public void delete(String existItem) {
		String SQL = "delete from rbt_plugin_count_in where plugin_key = ?";
		try {
			jdbcTemplateObject.update(SQL, existItem);
		} catch (EmptyResultDataAccessException e) {
			// empty
		}
	}
}

