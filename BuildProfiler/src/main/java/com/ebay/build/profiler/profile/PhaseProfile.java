package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.model.Phase;
import com.ebay.build.profiler.model.Project;
import com.ebay.build.profiler.util.Timer;

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
