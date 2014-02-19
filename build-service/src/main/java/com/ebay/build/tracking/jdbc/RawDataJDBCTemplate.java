package com.ebay.build.tracking.jdbc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class RawDataJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public int getTotalDuration(final int sessionID) {
		final String SQL = "select sum(duration) from rbt_raw_data where session_id = ?";
		return jdbcTemplateObject.queryForInt(SQL, new Object[] {sessionID});
	}
	
	public Map<Integer, Integer> getMapTotalDuration(String sessionSQL) {
		final String SQL = "select session_id, sum(duration) sum_of_duration from rbt_raw_data where " 
				//+ " session_id > " + startSession + " and session_id < " + endSession
				+ " session_id in (" + sessionSQL + ")"
				+ " group by session_id ";
		List<Map<String, Object>> results = jdbcTemplateObject.queryForList(SQL, new Object[] {});
		
		Map<Integer, Integer> durationMap = new HashMap<Integer, Integer>();
		for (Map<String, Object> m : results) {
			durationMap.put(((BigDecimal)m.get("session_id")).intValue(),  ((BigDecimal)m.get("sum_of_duration")).intValue());
		} 
		return durationMap;
	}
	
	public int getTotalExcludedPluginDuration(final int sessionID) {
		final String SQL = "select sum(duration) from rbt_raw_data r where r.session_id = ? and r.plugin_key not in (select plugin_key from rbt_plugin_count_in)";
		return jdbcTemplateObject.queryForInt(SQL, new Object[] {sessionID});
	}
	
	public Map<Integer, Integer> getMapExcludedPluginDuration(String sessionSQL) {
		final String SQL = "select r.session_id, sum(r.duration) sum_of_duration from rbt_raw_data r where r.plugin_key not in (select plugin_key from rbt_plugin_count_in) " 
				+ " and session_id in (" + sessionSQL + ")"
				+ " group by session_id ";
		List<Map<String, Object>> results = jdbcTemplateObject.queryForList(SQL, new Object[] {});
		
		Map<Integer, Integer> durationMap = new HashMap<Integer, Integer>();
		for (Map<String, Object> m : results) {
			durationMap.put(((BigDecimal)m.get("session_id")).intValue(),  ((BigDecimal)m.get("sum_of_duration")).intValue());
		} 
		return durationMap;
	}
	
	public Map<Integer, Integer> getMapIncludedPluginDuration(String sessionSQL, String keyList) {
		final String SQL = "select r.session_id, sum(r.duration) sum_of_duration from rbt_raw_data r where r.plugin_key in (" + keyList + ") " 
				+ " and session_id in (" + sessionSQL + ")"
				+ " group by session_id ";
		List<Map<String, Object>> results = jdbcTemplateObject.queryForList(SQL, new Object[] {});
		
		Map<Integer, Integer> durationMap = new HashMap<Integer, Integer>();
		for (Map<String, Object> m : results) {
			durationMap.put(((BigDecimal)m.get("session_id")).intValue(),  ((BigDecimal)m.get("sum_of_duration")).intValue());
		} 
		return durationMap;
	}
	
}
