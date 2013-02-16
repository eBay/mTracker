package com.ebay.build.cal.query;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import com.ebay.build.cal.query.common.CALServiceException;
import com.ebay.build.cal.query.common.IServiceResponse;


public class GetMachinesResponse implements IServiceResponse {
	
	private List<CALMachine> pools  = new ArrayList<CALMachine>();
	
	public GetMachinesResponse(String responseString) throws CALServiceException{
		parseResponseString(responseString);
	}
	
	private void parseResponseString(String responseString) throws CALServiceException {
		JSONObject json = (JSONObject) JSONSerializer.toJSON( responseString );  

		JSONArray results = json.getJSONArray("data");
		
		for (int i = 0; i < results.size(); i++) {
			CALMachine pool = new CALMachine();
			pool.setMachineName(results.getString(i));
			
			this.pools.add(pool);
		}	
	}
	
	public List<CALMachine> getMachines(){
		return pools;
	}
	
}
