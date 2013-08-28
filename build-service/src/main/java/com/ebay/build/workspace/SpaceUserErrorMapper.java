package com.ebay.build.workspace;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ebay.build.reliability.ErrorCode;

public class SpaceUserErrorMapper implements RowMapper<ErrorCode> {
	public ErrorCode mapRow(ResultSet rs, int arg1) throws SQLException {
		ErrorCode errorCode = new ErrorCode();
		errorCode.setName(rs.getString("name"));
		errorCode.setCount(rs.getInt("total"));
		errorCode.setDescription(rs.getString("cause"));
		return errorCode;
	}
}
