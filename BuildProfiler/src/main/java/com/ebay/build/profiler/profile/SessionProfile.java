package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.CALLogger;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;

public class SessionProfile extends Profile {
	
	private List<ProjectProfile> projectProfiles;
	
	private CalTransaction sesionTransaction;
	private ExecutionEvent event;

	public SessionProfile(ExecutionEvent event) {
		super(new Timer());
				
		this.projectProfiles = new ArrayList<ProjectProfile>();
		this.event = event;
		
		if(CALLogger.isCalInitialized()) {
			sesionTransaction = CALLogger.startCALTransaction("Session" , event.getSession().getGoals().toString());
		}
		
	}
	
	public SessionProfile() {
		super(new Timer());
		this.projectProfiles = new ArrayList<ProjectProfile>();
		
		if(CALLogger.isCalInitialized()) {
			sesionTransaction = CALLogger.startCALTransaction("Session" , "data");
		}
		
	}

	public void addProjectProfile(ProjectProfile projectProfile) {
		projectProfiles.add(projectProfile);
	}

	public List<ProjectProfile> getProjectProfiles() {
		return projectProfiles;
	}
	
	@Override
	public void stop() {
		if(sesionTransaction != null) {
			if(event.getSession().getResult().getExceptions().size() > 0) {
				CALLogger.endCALTransaction(sesionTransaction,"FAILED", event.getException());
			} else {
				CALLogger.endCALTransaction(sesionTransaction, "0");
			}
		}
		
		
		super.stop();
	}
}
