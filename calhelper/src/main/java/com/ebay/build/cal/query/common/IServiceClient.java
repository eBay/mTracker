package com.ebay.build.cal.query.common;



public interface IServiceClient {
	IServiceResponse get(IServiceRequest request) throws CALServiceException;
}
