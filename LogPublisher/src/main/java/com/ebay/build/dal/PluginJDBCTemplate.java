package com.ebay.build.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ebay.build.cal.model.Plugin;
import com.ebay.build.cal.query.utils.StringUtils;

public class PluginJDBCTemplate {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);
	}

	public int create(final Plugin plugin) {
		final String SQL = "insert into RBT_PLUGIN (plugin_key, group_id, artifact_id, version, phase) values (?, ?, ?, ?, ?)";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplateObject.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement(SQL,
						new String[] { "id" });
				ps.setString(1, plugin.getPluginKey());
				ps.setString(2, plugin.getGroupId());
				ps.setString(3, plugin.getArtifactId());
				ps.setString(4, plugin.getVersion());
				ps.setString(5, plugin.getPhaseName());

				return ps;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}
	
	public Plugin getPlugin(String pluginKey) {
		if (StringUtils.isEmpty(pluginKey)) {
			return null;
		}
		String SQL = "select * from RBT_PLUGIN where plugin_key = ?";
		try {
			Plugin plugin = jdbcTemplateObject.queryForObject(SQL,
					new Object[] { pluginKey }, new PluginMapper());
			return plugin;
		} catch (EmptyResultDataAccessException e) {
			// empty
		}
		return null;
	}
}

