package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import com.ebay.build.profiler.util.Timer;

public class PhaseProfile extends Profile {

	private String phase;
	private List<MojoProfile> mojoProfiles;

	public PhaseProfile(String phase) {
		super(new Timer());
		this.phase = phase;
		this.mojoProfiles = new ArrayList<MojoProfile>();
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

}
