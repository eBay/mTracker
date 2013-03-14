package com.ebay.build.cal.query.common;

import com.ebay.build.cal.query.GetMachinesRequest;
import com.ebay.build.cal.query.GetMachinesResponse;
import com.ebay.build.cal.query.GetPoolsRequest;
import com.ebay.build.cal.query.GetPoolsResponse;
import com.ebay.build.cal.query.GetRawLogRequest;
import com.ebay.build.cal.query.GetRawLogResponse;

public enum ServiceResponseFactory {
	INSTANCE;
	
	public IServiceResponse getResponse(IServiceRequest request, String respJsonString) throws CALServiceException{
		if (request instanceof GetPoolsRequest){
			return new GetPoolsResponse(request.getRequestPath(), respJsonString);
		}
		
		if (request instanceof GetMachinesRequest){
			return new GetMachinesResponse(respJsonString);
		}
		
		if (request instanceof GetRawLogRequest) {
			return new GetRawLogResponse(request.getRequestPath(), respJsonString);
		}
		
		return null;
	}
	
}
