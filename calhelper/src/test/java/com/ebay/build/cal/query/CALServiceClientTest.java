package com.ebay.build.cal.query;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;

import com.ebay.build.cal.query.common.CALServiceException;

public class CALServiceClientTest {

	@Test
	public void getPools() throws CALServiceException{
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, 1, 15, 23, 0);
		
		GetPoolsRequest request = new GetPoolsRequest("prod", cal.getTime());
		GetPoolsResponse response = (GetPoolsResponse) client.get(request);
			
		assertTrue(response.getPools().size()>0);	
	}
	
	@Test
	public void getMachines() throws CALServiceException{
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, 1, 15, 23, 0);
		
		GetMachinesRequest request = new GetMachinesRequest("prod", "appcenter", cal.getTime());
		GetMachinesResponse response = (GetMachinesResponse) client.get(request);
			
		assertTrue(response.getMachines().size()>0);	
	}
}
