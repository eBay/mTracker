package com.ebay.build.profiler.profile;

import java.util.ArrayList;

import java.util.List;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;

public class SessionProfile extends Profile {
	
	private List<ProjectProfile> projectProfiles;
	
	private CalTransaction sesionTransaction;

	public SessionProfile(Context c, ExecutionEvent event) {
		super(new Timer(), event, c);
		
		this.projectProfiles = new ArrayList<ProjectProfile>();
		
		if(isCalInitialized()) {
			String goal = "";
			if (event != null) {
				goal = event.getSession().getGoals().toString();
			}

			if (isInJekins()) {
				getSession().setGoals(goal);
			} else {
				sesionTransaction = calogger.startCALTransaction("Session" , goal);
			}
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
		super.stop();
		
		if(isCalInitialized()) {
			endTransaction(sesionTransaction);
		}
	}
}
