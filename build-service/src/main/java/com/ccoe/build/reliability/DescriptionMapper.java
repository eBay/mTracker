package com.ccoe.build.reliability;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class DescriptionMapper implements RowMapper<ErrorCode>{

	@Override
	public ErrorCode mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		ErrorCode errorDescription = new ErrorCode();
		errorDescription.setName(rs.getString("error_code"));
		errorDescription.setDescription(rs.getString("description"));
		return errorDescription;
	}
}
