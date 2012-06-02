package com.ebay.build.profiler.profile;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;

import com.ebay.build.profiler.util.Timer;

public class ProjectProfile extends Profile {

	private MavenProject project;
	private List<PhaseProfile> phaseProfiles;
	private String status;
	private String message;

	private String projectGroupId;
	private String projectArtifactId;
	private String projectVersion;

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



}