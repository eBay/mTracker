package com.ebay.build.cal.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.ebay.build.cal.query.common.CALServiceException;
import com.ebay.build.cal.query.common.IServiceResponse;


public class GetPoolsResponse implements IServiceResponse {
	
	private List<CALPool> pools  = new ArrayList<CALPool>();
	private String requestPath;
	
	public GetPoolsResponse(String requestPath, String responseString) throws CALServiceException{
		this.requestPath = requestPath;
		parseResponseString(responseString);
	}
	
	private void parseResponseString(String responseString) throws CALServiceException {
		JSONObject json = (JSONObject) JSONSerializer.toJSON( responseString );  

		JSONArray results = json.getJSONArray("data");
		
		for (int i = 0; i < results.size(); i++) {
			CALPool pool = new CALPool();
			pool.setRequestPath(this.requestPath);
			pool.setPoolName(results.getString(i));
			
			this.pools.add(pool);
		}	
	}
	
	public List<CALPool> getPools(){
		return pools;
	}
	
}
