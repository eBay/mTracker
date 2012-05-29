package com.ebay.build.profiler.lifecycle;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;

import com.ebay.build.profiler.profile.MojoProfile;
import com.ebay.build.profiler.profile.PhaseProfile;
import com.ebay.build.profiler.profile.ProjectProfile;
import com.ebay.build.profiler.profile.SessionProfile;

/**
 * MavenLifecycleProfiler will profile the maven build.
 * 
 * @author kmuralidharan
 * 
 */
public class MavenLifecycleProfiler extends AbstractEventSpy {

	private SessionProfile sessionProfile;
	private ProjectProfile projectProfile;
	private PhaseProfile phaseProfile;
	private MojoProfile mojoProfile;

	@Override
	public void init(Context context) throws Exception {

	}

	@Override
	public void onEvent(Object event) throws Exception {
		if (event instanceof ExecutionEvent) {
			ExecutionEvent executionEvent = (ExecutionEvent) event;
			Type executionEventType = executionEvent.getType();

			if (executionEventType == ExecutionEvent.Type.MojoStarted) {
				String phase = executionEvent.getMojoExecution()
						.getLifecyclePhase();
				if (phaseProfile == null) {
					phaseProfile = new PhaseProfile(phase);
				} else if (!phaseProfile.getPhase().equals(phase)) {
					phaseProfile.stop();
					projectProfile.addPhaseProfile(phaseProfile);
					phaseProfile = new PhaseProfile(phase);
				}
				mojoProfile = new MojoProfile(executionEvent.getMojoExecution());
			} else if (executionEventType == ExecutionEvent.Type.MojoSucceeded) {
				mojoProfile.stop();
				mojoProfile.setStatus("SUCCESS");
				mojoProfile.setExecutionMsg(executionEvent.getException().getMessage());
		        phaseProfile.addMojoProfile(mojoProfile);     
			} else if (executionEventType == ExecutionEvent.Type.MojoFailed) {
				mojoProfile.stop();
		        phaseProfile.addMojoProfile(mojoProfile);     
			} else if (executionEventType == ExecutionEvent.Type.ProjectDiscoveryStarted) {

			} else if (executionEventType == ExecutionEvent.Type.ProjectStarted) {
				projectProfile = new ProjectProfile(executionEvent.getProject());
				sessionProfile.addProjectProfile(projectProfile);
			} else if (executionEventType == ExecutionEvent.Type.ProjectSucceeded) {
				projectProfile.stop();
				projectProfile.setStatus("SUCCESS");
			} else if (executionEventType == ExecutionEvent.Type.ProjectFailed) {
				projectProfile.stop();
				projectProfile.setStatus("FAILED");
				projectProfile.setMessage(executionEvent.getException()
						.getMessage());
			} else if (executionEventType == ExecutionEvent.Type.SessionStarted) {
				sessionProfile = new SessionProfile();
			} else if (executionEventType == ExecutionEvent.Type.SessionEnded) {
				sessionProfile.stop();
			}
		}
	}

	@Override
	public void close() throws Exception {

	}
}
