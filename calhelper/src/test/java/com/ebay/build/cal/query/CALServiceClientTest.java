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
		cal.set(2013, 2, 1, 2, 0);
		
		GetPoolsRequest request = new GetPoolsRequest("raptor-build-trackingsql", cal.getTime());
		//GetPoolsRequest request = new GetPoolsRequest("prod", cal.getTime());
		GetPoolsResponse response = (GetPoolsResponse) client.get(request);
		
		System.out.println(response.getPools());
			
		assertTrue(response.getPools().size()>0);	
	}
	
	@Test
	public void getMachines() throws CALServiceException{
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, 2, 1, 2, 0);
		
		GetMachinesRequest request = new GetMachinesRequest("raptor-build-trackingsql", "caltestparent-mavenbuild", cal.getTime());
		GetMachinesResponse response = (GetMachinesResponse) client.get(request);
			
		assertTrue(response.getMachines().size()>0);	
	}
	
	@Test
	public void getRawLog() throws CALServiceException {
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, 2, 1, 2, 0);
		
		GetRawLogRequest request = new GetRawLogRequest("raptor-build-trackingsql", "caltestparent-mavenbuild", "d-shc-00436998.corp.ebay.com", cal.getTime());
		GetRawLogResponse response = (GetRawLogResponse) client.get(request);
		//System.out.println(response.getRawLog());
		
		assertTrue(response.getRawLog().length() > 0);
		///assertTrue(response.getMachines().size()>0);	
	}
}
