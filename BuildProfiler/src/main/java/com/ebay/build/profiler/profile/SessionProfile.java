package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import com.ebay.build.profile.util.Timer;

public class SessionProfile extends Profile {
	private List<ProjectProfile> projectProfiles;

	public SessionProfile() {
		super(new Timer());
		this.projectProfiles = new ArrayList<ProjectProfile>();
	}

	public void addProjectProfile(ProjectProfile projectProfile) {
		projectProfiles.add(projectProfile);
	}

	public List<ProjectProfile> getProjectProfiles() {
		return projectProfiles;
	}
}
