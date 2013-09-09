package com.ebay.build.profiler.lifecycle;



import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
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

	private DArtifacts preArtifacts;
	private DArtifacts dArtifacts;
	private Session session = new Session();
	private BuildServiceConfigBean mddaConfig;
	private boolean isPart1 = true;
	private File userSettingFile;
	private File globalSettingFile;
	private boolean debug;
	private boolean skipMDDA = false;
	
	
	@Override
	public void init(Context context) throws Exception {
		System.out.println( " __  __" );
		System.out.println( "|  \\/  |__ _Apache__ ___" );
		System.out.println( "| |\\/| / _` \\ V / -_) ' \\  ~ eBay Raptor Build Profiling ~" );
		System.out.println( "|_|  |_\\__,_|\\_/\\___|_||_|");
		System.out.println( "" );

		this.context = context;
		
		dArtifacts = new DArtifacts();		
		
		preArtifacts = new DArtifacts();
		
		this.context.getData().put(session.getClass().toString(), session);
		
		mddaConfig = new BuildServiceConfig().get("com.ebay.build.profiler.mdda");
		
		this.context.getData().put(BuildServiceConfigBean.class.toString(), mddaConfig);
	}
	
	private boolean isSnapshot(DArtifact dArtifact) {
		return dArtifact.getRepositoryId().equalsIgnoreCase("SNAPSHOTS") 
				|| dArtifact.getVersion().equalsIgnoreCase("SNAPSHOT"); 
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
		dItem.setVersion(artifact.getBaseVersion());
		dItem.setClassifier(artifact.getClassifier());
		dItem.setExtension(artifact.getExtension());
		
		if (artifact.getFile() != null) {
			dItem.setSize(artifact.getFile().length());
		}
		
		dItem.generateUrl();

		// filter snapshot version
		if (!isSnapshot(dItem)) {
			boolean isNewItem = true;
			// keep the download-list
			List<DArtifact> downloadList;
			if(isPart1) {
				downloadList = preArtifacts.getDArtifactList();
			} else {
				downloadList = dArtifacts.getDArtifactList();	
			}
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
		
		
		if (event instanceof SettingsBuildingRequest) {
			
			SettingsBuildingRequest settingBuildingRequest = (SettingsBuildingRequest) event;
			
			userSettingFile = settingBuildingRequest.getUserSettingsFile();
			
			globalSettingFile = settingBuildingRequest.getGlobalSettingsFile();
			
			
		}
		if (event instanceof MavenExecutionRequest) {

			MavenExecutionRequest mer = (MavenExecutionRequest) event;

			debug = (mer.getLoggingLevel() == MavenExecutionRequest.LOGGING_LEVEL_DEBUG);
		
			context.getData().put("baseAdd",mer.getBaseDirectory());
			
			skipMDDA = mer.getUserProperties().getProperty("skipMDDA") != null;

			context.getData().put("skipMDDA", skipMDDA);
		}
		
		
		if (mddaConfig.isGlobalSwitch() && !skipMDDA) {
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
				discoveryProfile = new DiscoveryProfile(context, executionEvent, userSettingFile, globalSettingFile,debug);

			} else if (executionEvent.getType() == ExecutionEvent.Type.SessionStarted) {
				sessionProfile = new SessionProfile(context, executionEvent, debug);
				//split the download-list into two lists
				isPart1 = false;
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
		
		System.out.println("[INFO] MDDA collected " + preArtifacts.getDArtifactList().size() + " preArtifacts.");
		System.out.println("[INFO] MDDA collected " + dArtifacts.getDArtifactList().size() + " dArtifacts.");
		
		if (!preArtifacts.getDArtifactList().isEmpty()) {
			FileProperties fp = discoveryProfile.getFp();
			if (discoveryProfile.XmlSettingChanged()) {
				System.out.println("[INFO] MDDA creating a new " + fp.getPreCacheListFile().getAbsolutePath());
				XMLConnector.marshal(fp.getPreCacheListFile(), preArtifacts);
			}
		}
		
		if (!dArtifacts.getDArtifactList().isEmpty()) {
			FileProperties fp = sessionProfile.getFp();
			if (sessionProfile.settingChanged()) {
				System.out.println("[INFO] MDDA creating a new " + fp.getDepCacheListFile().getAbsolutePath());			
				XMLConnector.marshal(fp.getDepCacheListFile(), dArtifacts);
			} else {
				if (fp.getDepCacheListFile().exists()) {
					System.out.println("[INFO] MDDA updating an existing " + fp.getDepCacheListFile().getAbsolutePath());
					DArtifacts existingArtifacts = XMLConnector.unmarshal(fp.getDepCacheListFile());
					Set<DArtifact> artifactSet = new HashSet<DArtifact>();
					artifactSet.addAll(existingArtifacts.getDArtifactList());
					artifactSet.addAll(dArtifacts.getDArtifactList());

					dArtifacts.getDArtifactList().clear();
					dArtifacts.getDArtifactList().addAll(artifactSet);
					XMLConnector.marshal(fp.getDepCacheListFile(), dArtifacts);
				}
			}
		}
	}
}
