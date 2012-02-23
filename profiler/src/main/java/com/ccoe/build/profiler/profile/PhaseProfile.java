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
import java.util.Date;
import java.util.List;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ccoe.build.core.model.Phase;
import com.ccoe.build.core.model.Project;
import com.ccoe.build.profiler.util.Timer;

public class PhaseProfile extends Profile {

	private String phaseName;
	private List<MojoProfile> mojoProfiles;
	
	private Phase phase = new Phase();

	public PhaseProfile(Context c, String p, ExecutionEvent event) {
		super(new Timer(), event, c);
		this.phaseName = p;
		this.mojoProfiles = new ArrayList<MojoProfile>();
		
		this.event = event;
		
		if (getSession() != null) {
			Project project = getSession().getCurrentProject();
			phase.setName(phaseName);
			phase.setStartTime(new Date(this.getTimer().getStartTime()));
			project.getPhases().add(phase);
		}
	}

	public void addMojoProfile(MojoProfile mojoProfile) {
		mojoProfiles.add(mojoProfile);
	}

	public String getPhase() {
		return phaseName;
	}

	public List<MojoProfile> getMojoProfiles() {
		return mojoProfiles;
	}

	@Override
	public void stop() {
		super.stop();

		String status = endTransaction();
		
		phase.setDuration(this.getElapsedTime());
		phase.setStatus(status);
	}
}
