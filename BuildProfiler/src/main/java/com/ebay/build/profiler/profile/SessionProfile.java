package com.ebay.build.profiler.profile;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;

import com.ebay.build.profiler.mdda.util.FileProperties;
import com.ebay.build.profiler.mdda.util.PreDownload;
import com.ebay.build.profiler.util.Timer;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.profiler.utils.MD5Generator;

public class SessionProfile extends Profile {
	
	private List<ProjectProfile> projectProfiles;
	
	private PreDownloadProfile pdProfile;

	boolean settingChanged = true;
	
	public PreDownloadProfile getPdProfile() {
		return pdProfile;
	}
	
	public boolean settingChanged() {
		return settingChanged;
	}

	public SessionProfile(Context c, ExecutionEvent event) {
		super(new Timer(), event, c);
		
		this.projectProfiles = new ArrayList<ProjectProfile>();

		String goal = "";
		if (event != null) {
			goal = event.getSession().getGoals().toString();
		}

		if (getSession() != null) {
			getSession().setGoals(goal);
		}
		
		if (event != null) {
			mddaMain(event);
		}
	}
	
	private void mddaMain(ExecutionEvent event) {
		if (!this.getConfig().isGlobalSwitch()) {
			System.out.println("[INFO] MDDA turned off");
			return;
		} else {
			System.out.println("[INFO] MDDA turned on");
		}
		
		ArtifactRepository lr = event.getSession().getLocalRepository();
		File localrepo = null;
		try {
			localrepo = new File(new URL(lr.getUrl()).toURI());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		List<MavenProject> projects = event.getSession().getProjects();
		
		String fullString = getRemoteRepositories(projects);

		String md5 = MD5Generator.generateMd5(fullString);

		// Judgment : whether the repository list has been changed

		String appName = null;
		appName = getSession().getAppName();
		if (appName == null) {
			System.out.println("[INFO] MDDA cannot find application name, MDDA exit.");
			return;
		}
		FileProperties fp = new FileProperties(appName); 
		
		File md5File = fp.getRemoteRepoCacheMd5File();

		if (md5File.exists()) {
			String oldmd5 = FileUtils.readFile(md5File);

			System.out.println("[INFO] MDDA Previous Settings MD5: " + oldmd5);
			System.out.println("[INFO] MDDA Current  Settings MD5: " + md5);
			
			if (oldmd5.equals(md5)) {
				settingChanged = false;
			}
		}

		// if no change ,set up our accelerator
		pdProfile = new PreDownloadProfile(event);
		
		if (!settingChanged) {
			PreDownload.start(fp.getDepCacheListFile(), localrepo);
		} else {
			// else , records the new md5 and repo-message,let maven build
			// by it self
			FileUtils.writeToFile(md5File, md5);

			File repoFile = fp.getRemoteRepoCacheFile();
			FileUtils.writeToFile(repoFile, fullString);
		}
		pdProfile.stop();
	}
	
	private String getRemoteRepositories(List<MavenProject> projects) {
		String fullString = "";
		for (MavenProject project : projects) {
			// get the remoteRepo message
			List<ArtifactRepository> remoteRepos = project.getRemoteArtifactRepositories();
			remoteRepos.addAll(project.getPluginArtifactRepositories());
			String projectName = "Project : " + project.getName() + "\n";

			fullString += projectName;

			for (ArtifactRepository repository : remoteRepos) {
				String repoContent = "    Remote Repo:\n" + repository;
				fullString += repoContent;
			}
		}
		return fullString;
	}

	public void addProjectProfile(ProjectProfile projectProfile) {
		projectProfiles.add(projectProfile);
	}

	public List<ProjectProfile> getProjectProfiles() {
		return projectProfiles;
	}
	
	@Override
	public void stop() {
		super.stop();
		endTransaction();
	}
}
