package com.ebay.build.profiler.profile;

import java.net.URL;

import com.ebay.build.profiler.util.CALLogger;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private CalTransaction discoveryTransaction;

	public DiscoveryProfile() {
		super(new Timer());
		
		URL calConfig = getClass().getClassLoader().getResource("cal.properties");
		boolean isCalEnabled = CALLogger.initialize(calConfig, "RaptorBuild");
		
		if(isCalEnabled) {
			discoveryTransaction = CALLogger.startCALTransaction("DEV" , "data");
		}
	}
	
	@Override
	public void stop() {
		CALLogger.endCALTransaction(discoveryTransaction, "0");
		super.stop();
	}

}
