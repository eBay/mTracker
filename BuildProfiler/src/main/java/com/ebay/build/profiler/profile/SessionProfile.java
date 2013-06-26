package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.Timer;

public class SessionProfile extends Profile {
	
	private List<ProjectProfile> projectProfiles;
	
	public SessionProfile(Context c, ExecutionEvent event) {
		super(new Timer(), event, c);
		
		this.projectProfiles = new ArrayList<ProjectProfile>();

		String goal = "";
		if (event != null) {
			goal = event.getSession().getGoals().toString();
		}

		if (getSession() != null) {
			getSession().setGoals(goal);
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
		endTransaction();
	}
}
