package com.ebay.build.profiler.profile;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.util.Timer;

public class PreDownloadProfile extends Profile {


	public PreDownloadProfile(Context c, ExecutionEvent event) {
		super(new Timer(), event, c);
		
	}

	@Override
	public void stop() {
		super.stop();
	}
}
