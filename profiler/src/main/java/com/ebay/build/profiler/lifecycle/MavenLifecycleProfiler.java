package com.ebay.build.profiler.lifecycle;


import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;

import com.ebay.build.core.model.Session;
import com.ebay.build.profiler.profile.DiscoveryProfile;
import com.ebay.build.profiler.profile.MojoProfile;
import com.ebay.build.profiler.profile.PhaseProfile;
import com.ebay.build.profiler.profile.ProjectProfile;
import com.ebay.build.profiler.profile.SessionProfile;
import com.ebay.build.profiler.render.OutputRenderer;

/**
 * MavenLifecycleProfiler will profile the maven build.
 * 
 * @author kmuralidharan
 * @author mmao
 */

@Named
@Singleton
public class MavenLifecycleProfiler extends AbstractEventSpy {

	private DiscoveryProfile discoveryProfile;
	private SessionProfile sessionProfile;
	private ProjectProfile projectProfile;
	private PhaseProfile phaseProfile;
	private MojoProfile mojoProfile;
	private Context context; 

	private Session session = new Session();
	private File userSettingFile;
	private File globalSettingFile;
	private boolean debug;
	
	
	@Override
	public void init(Context context) throws Exception {
		System.out.println( " __  __" );
		System.out.println( "|  \\/  |__ _Apache__ ___" );
		System.out.println( "| |\\/| / _` \\ V / -_) ' \\  ~ eBay Maven Build Profiler ~" );
		System.out.println( "|_|  |_\\__,_|\\_/\\___|_||_|");
		System.out.println( "" );

		this.context = context;
		
		this.context.getData().put(session.getClass().toString(), session);
		
	}
	
	@Override
	public void onEvent(Object event) throws Exception {
		
		if (event instanceof SettingsBuildingRequest) {
			SettingsBuildingRequest settingBuildingRequest = (SettingsBuildingRequest) event;
			userSettingFile = settingBuildingRequest.getUserSettingsFile();
			globalSettingFile = settingBuildingRequest.getGlobalSettingsFile();
		}
		
		if (event instanceof MavenExecutionRequest) {
			MavenExecutionRequest mer = (MavenExecutionRequest) event;
			debug = (mer.getLoggingLevel() == MavenExecutionRequest.LOGGING_LEVEL_DEBUG);
			context.getData().put("baseAdd",mer.getBaseDirectory());
		}
		
		if (event instanceof ExecutionEvent) {

			ExecutionEvent executionEvent = (ExecutionEvent) event;

			if (executionEvent.getType() == ExecutionEvent.Type.ProjectDiscoveryStarted) {
				discoveryProfile = new DiscoveryProfile(context, executionEvent, userSettingFile, globalSettingFile,debug);

			} else if (executionEvent.getType() == ExecutionEvent.Type.SessionStarted) {
				sessionProfile = new SessionProfile(context, executionEvent, debug);
			} else if (executionEvent.getType() == ExecutionEvent.Type.SessionEnded) {
				projectProfile.addPhaseProfile(phaseProfile);
				
				//if can accelerate,we will not generate the dependency file
				
				sessionProfile.stop();
				discoveryProfile.stop();
				OutputRenderer renderer = new OutputRenderer(sessionProfile, discoveryProfile);
				renderer.renderToScreen();
				// renderer.renderToJSON();
			} else if (executionEvent.getType() == ExecutionEvent.Type.ProjectStarted) {
				projectProfile = new ProjectProfile(context, executionEvent.getProject(), executionEvent);
			} else if (executionEvent.getType() == ExecutionEvent.Type.ProjectSucceeded
					|| executionEvent.getType() == ExecutionEvent.Type.ProjectFailed) {

				if (phaseProfile != null)
					phaseProfile.stop();
				projectProfile.stop();
				sessionProfile.addProjectProfile(projectProfile);
			} else if (executionEvent.getType() == ExecutionEvent.Type.MojoStarted) {
				String phase = executionEvent.getMojoExecution()
						.getLifecyclePhase();
				if (phaseProfile == null) {
					phaseProfile = new PhaseProfile(context, phase, executionEvent);
				} else if (phase == null) {
					phaseProfile.stop();
					projectProfile.addPhaseProfile(phaseProfile);
					phaseProfile = new PhaseProfile(context, "default", executionEvent);
				} else if (!phase.equals(phaseProfile.getPhase())) {
					phaseProfile.stop();
					projectProfile.addPhaseProfile(phaseProfile);
					phaseProfile = new PhaseProfile(context, phase, executionEvent);
				}
				mojoProfile = new MojoProfile(context, executionEvent.getMojoExecution(), executionEvent);
			} else if (executionEvent.getType() == ExecutionEvent.Type.MojoSucceeded
					|| executionEvent.getType() == ExecutionEvent.Type.MojoFailed) {
				mojoProfile.stop();
				phaseProfile.addMojoProfile(mojoProfile);
			}
		}
	}

	@Override
	public void close() throws Exception {

	}
}
