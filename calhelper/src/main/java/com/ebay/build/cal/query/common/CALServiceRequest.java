package com.ebay.build.cal.query.common;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;


public class CALServiceRequest implements IServiceRequest {
	private String requestPath;
	private MultivaluedMap<String, String> parameters;
	private String accept = MediaType.APPLICATION_JSON;
	
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

	public String getAccept() {
		return accept;
	}

	public void setAccept(String accept) {
		this.accept = accept;
	}

}
