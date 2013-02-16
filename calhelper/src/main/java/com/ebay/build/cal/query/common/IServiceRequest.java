package com.ebay.build.cal.query.common;

import javax.ws.rs.core.MultivaluedMap;

public interface IServiceRequest {
	String getRequestPath();
	MultivaluedMap<String, String> getParameters();
}
