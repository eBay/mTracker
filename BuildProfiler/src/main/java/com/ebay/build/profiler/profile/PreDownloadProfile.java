package com.ebay.build.profiler.profile;

import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.Timer;

public class PreDownloadProfile extends Profile {


	public PreDownloadProfile(ExecutionEvent event) {
		super(new Timer(), event, null);
		
	}

	@Override
	public void stop() {
		super.stop();
	}
}
