package com.ebay.build.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ebay.build.core.model.Plugin;

public class PluginMapper implements RowMapper<Plugin> {

	public Plugin mapRow(ResultSet rs, int arg1) throws SQLException {
		Plugin plugin = new Plugin();
		
		plugin.setId(rs.getInt("id"));
		plugin.setGroupId(rs.getString("group_id"));
		plugin.setArtifactId(rs.getString("artifact_id"));
		plugin.setVersion(rs.getString("version"));
		plugin.setPluginKey(rs.getString("plugin_key"));
		plugin.setPhaseName(rs.getString("phase"));
		
		return plugin;
	}
}
