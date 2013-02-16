package com.ebay.build.cal.query.common;

import com.ebay.build.cal.query.GetMachinesRequest;
import com.ebay.build.cal.query.GetMachinesResponse;
import com.ebay.build.cal.query.GetPoolsRequest;
import com.ebay.build.cal.query.GetPoolsResponse;

public enum ServiceResponseFactory {
	INSTANCE;
	
	public IServiceResponse getResponse(IServiceRequest request, String respJsonString) throws CALServiceException{
		if (request instanceof GetPoolsRequest){
			return new GetPoolsResponse(request.getRequestPath(), respJsonString);
		}
		
		if (request instanceof GetMachinesRequest){
			return new GetMachinesResponse(respJsonString);
		}
		
		return null;
	}
	
}
