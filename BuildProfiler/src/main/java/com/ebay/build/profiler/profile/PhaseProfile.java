package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.CALLogger;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;

public class PhaseProfile extends Profile {

	private String phase;
	private List<MojoProfile> mojoProfiles;
	
	private CalTransaction phaseTransaction;
	private ExecutionEvent event;

	public PhaseProfile(String phase) {
		super(new Timer());
		this.phase = phase;
		this.mojoProfiles = new ArrayList<MojoProfile>();
	}
	
	public PhaseProfile(String phase, ExecutionEvent event) {
		super(new Timer());
		this.phase = phase;
		this.mojoProfiles = new ArrayList<MojoProfile>();
		
		this.event = event;
		
		if(CALLogger.isCalInitialized()) {
			phaseTransaction = CALLogger.startCALTransaction("Phase" , phase);
		}
	}

	public void addMojoProfile(MojoProfile mojoProfile) {
		mojoProfiles.add(mojoProfile);
	}

	public String getPhase() {
		return phase;
	}

	public List<MojoProfile> getMojoProfiles() {
		return mojoProfiles;
	}

	@Override
	public void stop() {
		if(phaseTransaction != null) {
			if(event.getSession().getResult().getExceptions().size() > 0) {
				CALLogger.endCALTransaction(phaseTransaction,"FAILED", event.getException());
			} else {
				CALLogger.endCALTransaction(phaseTransaction, "0");
			}
		}
		
		
		super.stop();
	}
}
