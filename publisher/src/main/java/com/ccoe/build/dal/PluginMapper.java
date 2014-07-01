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

package com.ccoe.build.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ccoe.build.core.model.Plugin;

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
