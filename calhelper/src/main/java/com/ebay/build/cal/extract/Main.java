package com.ebay.build.cal.extract;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ebay.build.cal.model.Session;
import com.ebay.build.cal.processors.LineProcessor;
//import com.ebay.build.cal.processors.LoaderProcessor;
import com.ebay.build.cal.query.CALMachine;
import com.ebay.build.cal.query.CALPool;
import com.ebay.build.cal.query.CALServiceClient;
import com.ebay.build.cal.query.GetMachinesRequest;
import com.ebay.build.cal.query.GetMachinesResponse;
import com.ebay.build.cal.query.GetPoolsRequest;
import com.ebay.build.cal.query.GetPoolsResponse;
import com.ebay.build.cal.query.GetRawLogRequest;
import com.ebay.build.cal.query.GetRawLogResponse;
import com.ebay.build.cal.query.common.CALServiceException;

public class Main {
	private final String RAPTOR_BUILD_CAL_ENV = "raptor-build-trackingsql";
	private final CALServiceClient client = new CALServiceClient();
	private final LineProcessor lineProcessor = new LineProcessor();
	//private final LoaderProcessor loaderProcessor = new LoaderProcessor();

	public static void main(String[] args) {
		
		new Main().run();

	}
	
	private void run() { // ETL
		//Calendar cal = DateUtils.getAnHourBack();
		
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.MARCH, 13, 9, 0);

		List<CALPool> calPools = getPools(RAPTOR_BUILD_CAL_ENV, cal.getTime());
		
		if (calPools.isEmpty()) {
			System.out.println("No CAL Pools found");
			// TODO log if empty
		}
		
		for (CALPool pool : calPools) {
			System.out.println("CAL Pools found -- " + pool.getPoolName());
			List<CALMachine> machines = getMachines(RAPTOR_BUILD_CAL_ENV, cal.getTime(), pool.getPoolName());
			
			for (CALMachine machine : machines) {
				System.out.println("  CAL Machine found -- " + machine.getMachineName());
				String rawLog = getRawLog(RAPTOR_BUILD_CAL_ENV, cal.getTime(), pool.getPoolName(), machine.getMachineName());
				List<Session> sessions = lineProcessor.process(rawLog); // TODO rename to transformer.transform()
				for (Session session : sessions) {
					System.out.println("     Session -- " + session.getEnvironment() + " " + session.getStartTime());
					//loaderProcessor.process(session);
					// comment out this line due to the DAL related classes moved to LogPublisher.
					// this class was used for CAL now should be deprecated.
				}
			}
		}
		
		System.out.println("DONE!");
	}
	
	private String getRawLog(String calEnv, Date date, String calPool, String calMachine) {
		GetRawLogRequest request = new GetRawLogRequest(calEnv, calPool, calMachine, date);
		GetRawLogResponse response = null;
		try {
			response = (GetRawLogResponse) client.get(request);
		} catch (CALServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.getRawLog();
	}
	
	private List<CALMachine> getMachines(String calEnv, Date date, String calPool) {
		GetMachinesRequest request = new GetMachinesRequest(calEnv, calPool, date);
		GetMachinesResponse response = null;
		try {
			response = (GetMachinesResponse) client.get(request);
		} catch (CALServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.getMachines();
	}
	
	private List<CALPool> getPools(String calEnv, Date date) {
		GetPoolsRequest request = new GetPoolsRequest(calEnv, date);
		GetPoolsResponse response = null;
		try {
			response = (GetPoolsResponse) client.get(request);
		} catch (CALServiceException e) {
			e.printStackTrace();
		}
		return response.getPools();
	}
}
