package com.ebay.build.cal.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import com.ebay.build.cal.query.common.CALServiceException;
/* We are not using CAL as the data storage, so this test case is commented out */
@Deprecated
public class CALServiceClientTest {

	//@Test
	public void getPools() throws CALServiceException{
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.MARCH, 13, 3, 0);
		
		GetPoolsRequest request = new GetPoolsRequest("raptor-build-trackingsql", cal.getTime());
		//GetPoolsRequest request = new GetPoolsRequest("prod", cal.getTime());
		GetPoolsResponse response = (GetPoolsResponse) client.get(request);
		
		System.out.println(response.getPools());
			
		assertTrue(response.getPools().size()>0);
	}
	
	//@Test
	public void getMachines() throws CALServiceException{
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.MARCH, 13, 3, 0);
		
		GetMachinesRequest request = new GetMachinesRequest("raptor-build-trackingsql", "mobileweb-mavenbuild", cal.getTime());
		GetMachinesResponse response = (GetMachinesResponse) client.get(request);
			
		assertTrue(response.getMachines().size()>0);	
	}
	
	//@Test
	public void getRawLog() throws CALServiceException {
		CALServiceClient client = new CALServiceClient();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.MARCH, 13, 3, 0);
		
		GetRawLogRequest request = new GetRawLogRequest("raptor-build-trackingsql", "mobileweb-mavenbuild", "10.109.34.138", cal.getTime());
		GetRawLogResponse response = (GetRawLogResponse) client.get(request);
		assertNotNull(response);
		assertNotNull(response.getRawLog());
	}
}
