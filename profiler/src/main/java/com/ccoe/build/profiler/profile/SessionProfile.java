/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ccoe.build.profiler.util.Timer;

public class SessionProfile extends Profile {
	
	private List<ProjectProfile> projectProfiles;
	

	private boolean settingChanged = true;
	
	public boolean settingChanged() {
		return settingChanged;
	}

	public SessionProfile(Context c, ExecutionEvent event, boolean debug) {
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
