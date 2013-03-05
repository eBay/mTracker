package com.ebay.build.cal.query;

import java.net.URI;

import com.ebay.build.cal.query.common.CALServiceException;
import com.ebay.build.cal.query.common.IServiceClient;
import com.ebay.build.cal.query.common.IServiceRequest;
import com.ebay.build.cal.query.common.IServiceResponse;
import com.ebay.build.cal.query.common.ServiceResponseFactory;
import com.ebay.build.cal.query.utils.ServiceUtils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class CALServiceClient implements IServiceClient{
	
	private Client client;
	protected WebResource baseResource;
	
	public CALServiceClient(){
		client = ServiceUtils.createClient();
        //baseResource = client.resource("http://appmon.vip.ebay.com/logview/");
        baseResource = client.resource("http://appmon.vip.qa.ebay.com/logview/");
	}

	public void dispose(){
		this.client.destroy();
		baseResource = null;
	}
	
	public IServiceResponse get(IServiceRequest request) throws CALServiceException {
		String respString = getResponseJsonString(request);
		
		return ServiceResponseFactory.INSTANCE.getResponse(request, respString);
	}
	
	protected String getResponseJsonString(IServiceRequest request) throws CALServiceException{
		
		String responseStr = null;
		try{
			responseStr = baseResource
					.path(request.getRequestPath())
					.queryParams(request.getParameters())
					.accept(request.getAccept())
					.get(String.class);
		}catch(Exception e){
			throw new CALServiceException("Fail to talk with remote service API, maybe remote services aren't avalable now:"+e.getMessage(),e);
		}
		
		return responseStr;
	}

	//should be overided by sub classes
	protected URI getBaseURI() {return null;}

}
