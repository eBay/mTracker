package com.ebay.build.reliability;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class TopTenErrorMapper implements RowMapper<ErrorCode> {

	public ErrorCode mapRow(ResultSet rs, int arg1) throws SQLException {

		ErrorCode errorCode = new ErrorCode();
		errorCode.setName(rs.getString("filter"));
		errorCode.setCount(rs.getInt("c_filter"));
		
		return errorCode;
	}
}
