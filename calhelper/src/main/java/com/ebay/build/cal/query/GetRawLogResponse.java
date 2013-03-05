package com.ebay.build.cal.query;

import com.ebay.build.cal.query.common.CALServiceException;
import com.ebay.build.cal.query.common.IServiceResponse;

public class GetRawLogResponse implements IServiceResponse {
	private String rawLog;
	private String requestPath;
	
	public GetRawLogResponse(String requestPath, String responseString) throws CALServiceException{
		this.setRequestPath(requestPath);
		parseResponseString(responseString);
	}
	
	private void parseResponseString(String responseString) throws CALServiceException {
		this.setRawLog(responseString);
	}

	public String getRawLog() {
		return rawLog;
	}

	public void setRawLog(String rawLog) {
		this.rawLog = rawLog;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}
}
