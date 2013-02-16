package com.ebay.build.cal.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.ebay.build.cal.query.common.CALServiceRequest;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;



public class GetMachinesRequest extends CALServiceRequest{
	
	public GetMachinesRequest(String environment, String poolName, Date date) {
		super("environment/"+environment+"/pool/" + poolName + "/machines");
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		MultivaluedMap<String, String> parameters = new StringKeyIgnoreCaseMultivaluedMap<String>();
		
		List<String> values = new ArrayList<String>();
		values.add(dateFormatter.format(date));
		parameters.put("datetime", values);
		this.setParameters(parameters);
	}

}
