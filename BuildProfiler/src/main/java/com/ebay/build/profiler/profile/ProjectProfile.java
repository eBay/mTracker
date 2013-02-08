package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;

import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;

public class ProjectProfile extends Profile {

	private CalTransaction projectTransaction;
	private ExecutionEvent event;
	
	private MavenProject project;
	private List<PhaseProfile> phaseProfiles;
	private String status;
	private String message;

	private String projectGroupId;
	private String projectArtifactId;
	private String projectVersion;

	public ProjectProfile(MavenProject project, ExecutionEvent event) {
		super(new Timer());
		this.project = project;
		this.phaseProfiles = new ArrayList<PhaseProfile>();
		this.projectGroupId = project.getGroupId();
		this.projectArtifactId = project.getArtifactId();
		this.projectVersion = project.getVersion();
		this.event = event;
		
		if(calogger.isCalInitialized()) {
			projectTransaction = calogger.startCALTransaction("Project" , event.getProject().getId());
		}
	}
	
	public ProjectProfile(MavenProject project) {
		super(new Timer());
		this.project = project;
		this.phaseProfiles = new ArrayList<PhaseProfile>();
		this.projectGroupId = project.getGroupId();
		this.projectArtifactId = project.getArtifactId();
		this.projectVersion = project.getVersion();
	}

	public String getProjectGroupId() {
		return projectGroupId;
	}

	public String getProjectArtifactId() {
		return projectArtifactId;
	}

	public String getProjectVersion() {
		return projectVersion;
	}

	public void addPhaseProfile(PhaseProfile phaseProfile) {
		phaseProfiles.add(phaseProfile);
	}

	public List<PhaseProfile> getPhaseProfile() {
		return phaseProfiles;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getProjectName() {
		return new StringBuilder().append(projectGroupId).append(":")
				.append(projectArtifactId).append(":")
				.append(project.getVersion()).toString();
	}

	@Override
	public void stop() {
		if(projectTransaction != null) {
			if(event.getSession().getResult().getExceptions().size() > 0) {
				calogger.endCALTransaction(projectTransaction,"FAILED", event.getException());
			} else {
				calogger.endCALTransaction(projectTransaction, "0");
			}
		}
		
		
		super.stop();
	}

}