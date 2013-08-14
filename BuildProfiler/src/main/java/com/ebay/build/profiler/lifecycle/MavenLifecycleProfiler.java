package com.ebay.build.profiler.lifecycle;



import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.ArtifactRepository;

import com.ebay.build.profiler.mdda.bean.DArtifact;
import com.ebay.build.profiler.mdda.bean.DArtifacts;
import com.ebay.build.profiler.mdda.util.FileProperties;
import com.ebay.build.profiler.mdda.util.FormatTransform;
import com.ebay.build.profiler.mdda.util.XMLConnector;
import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.profile.DiscoveryProfile;
import com.ebay.build.profiler.profile.MojoProfile;
import com.ebay.build.profiler.profile.PhaseProfile;
import com.ebay.build.profiler.profile.ProjectProfile;
import com.ebay.build.profiler.profile.SessionProfile;
import com.ebay.build.profiler.render.OutputRenderer;
import com.ebay.build.service.config.BuildServiceConfig;
import com.ebay.build.service.config.BuildServiceConfigBean;

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
	private DArtifacts dArtifacts;

	private Session session = new Session();
	private BuildServiceConfigBean mddaConfig;
	
	@Override
	public void init(Context context) throws Exception {
		System.out.println( " __  __" );
		System.out.println( "|  \\/  |__ _Apache__ ___" );
		System.out.println( "| |\\/| / _` \\ V / -_) ' \\  ~ eBay Raptor Build Profiling ~" );
		System.out.println( "|_|  |_\\__,_|\\_/\\___|_||_|");
		System.out.println( "" );

		this.context = context;
		dArtifacts = new DArtifacts();		
		
		this.context.getData().put(session.getClass().toString(), session);
		
		mddaConfig = new BuildServiceConfig().get("com.ebay.build.profiler.mdda");
		this.context.getData().put(BuildServiceConfigBean.class.toString(), mddaConfig);
	}
	
	private boolean isSnapshot(DArtifact artifact) {
		return artifact.getRepositoryId().equalsIgnoreCase("SNAPSHOTS") 
				|| artifact.getVersion().equalsIgnoreCase("SNAPSHOT"); 
	}
	
	
	private void detectArtifactDownload(RepositoryEvent re){
		// prepare some data
		Artifact artifact = re.getArtifact();

		ArtifactRepository repo = re.getRepository();

		// Construct an downloadItem
		DArtifact dItem = new DArtifact();
	
		FormatTransform.parseRepository(repo.toString(), dItem);

		dItem.setGroup_id(artifact.getGroupId());
		dItem.setArtifact_id(artifact.getArtifactId());
		dItem.setVersion(artifact.getVersion());
		dItem.setClassifier(artifact.getClassifier());
		dItem.setExtension(artifact.getExtension());
		dItem.generateUrl();

		// filter snapshot version
		if (!isSnapshot(dItem)) {
			boolean isNewItem = true;
			// keep the download-list
			List<DArtifact> downloadList = dArtifacts.getDArtifactList();
			for (int i = 0; i < downloadList.size(); i++) {
				if (downloadList.get(i).equals(dItem)) {
					downloadList.set(i, dItem);
					isNewItem = false;
					break;
				}
			}
			if (isNewItem) {
				downloadList.add(dItem);
			}
		}
	}

	@Override
	public void onEvent(Object event) throws Exception {

		if (mddaConfig.isGlobalSwitch()) {
			if (event instanceof RepositoryEvent) {
				RepositoryEvent re = (RepositoryEvent) event;
			
				if (re.getType() == RepositoryEvent.EventType.ARTIFACT_DOWNLOADED) {
					detectArtifactDownload(re);
				}
			}
		}

		if (event instanceof ExecutionEvent) {

			ExecutionEvent executionEvent = (ExecutionEvent) event;

			if (executionEvent.getType() == ExecutionEvent.Type.ProjectDiscoveryStarted) {
				discoveryProfile = new DiscoveryProfile(context, executionEvent);

			} else if (executionEvent.getType() == ExecutionEvent.Type.SessionStarted) {
				sessionProfile = new SessionProfile(context, executionEvent);
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
				phaseProfile.stop();
				projectProfile.stop();
				sessionProfile.addProjectProfile(projectProfile);
			} else if (executionEvent.getType() == ExecutionEvent.Type.MojoStarted) {
				String phase = executionEvent.getMojoExecution()
						.getLifecyclePhase();
				if (phaseProfile == null) {
					phaseProfile = new PhaseProfile(context, phase, executionEvent);
				} else if (!phaseProfile.getPhase().equals(phase)) {
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
		if (sessionProfile.settingChanged()) {
			FileProperties fp = new FileProperties(session.getAppName());
			if (!dArtifacts.getDArtifactList().isEmpty()) {
				XMLConnector.marshal(fp.getDepCacheListFile(), dArtifacts);
			}
		}
	}

}
