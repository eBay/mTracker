package com.ebay.build.cal.query.common;

import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;


public class CALServiceRequest implements IServiceRequest {
	private String requestPath;
	private MultivaluedMap<String, String> parameters;
	
	public CALServiceRequest(String requestPath){
		this.requestPath = requestPath;
	}
	
	public MultivaluedMap<String, String> getParameters() {
		if (parameters == null){
			return new StringKeyIgnoreCaseMultivaluedMap<String>();
		}
		return parameters;
	}

	public void setParameters(MultivaluedMap<String, String> parameters) {
		this.parameters = parameters;
	}

	public String getRequestPath(){
		return requestPath;
	}

}
