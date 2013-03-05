package com.ebay.build.cal.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import com.ebay.build.cal.query.common.CALServiceRequest;
import com.sun.jersey.core.util.StringKeyIgnoreCaseMultivaluedMap;

public class GetRawLogRequest extends CALServiceRequest {

	public GetRawLogRequest(String environment, String pool, String machine, Date date) {
		super("environment/"+ environment +"/pool/" + pool + "/machine/" + machine + "/rawLog");
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		MultivaluedMap<String, String> parameters = new StringKeyIgnoreCaseMultivaluedMap<String>();

		List<String> values = new ArrayList<String>();
		values.add("0x1");
		parameters.put("thread", values);
		
		List<String> values1 = new ArrayList<String>();
		values1.add(dateFormatter.format(date));
		parameters.put("datetime", values1);
		this.setParameters(parameters);
		
		this.setAccept("text/plain");
	}
}
