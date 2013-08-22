 package com.ebay.build.profiler.profile;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;

import com.ebay.build.profiler.mdda.util.FileProperties;
import com.ebay.build.profiler.mdda.util.PreDownload;
import com.ebay.build.profiler.readers.ProcessHelper;
import com.ebay.build.profiler.util.Timer;
import com.ebay.build.profiler.utils.FileUtils;
import com.ebay.build.profiler.utils.MD5Generator;
import com.ebay.build.profiler.writers.SessionExporter;
import com.ebay.build.service.client.PostSessionClient;
//import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private static final String DELIMI = "----------";
	
	private boolean xmlSettingChanged = true;
	
	private FileProperties fp;
	
	public FileProperties getFp(){
		return fp;
	}
	
	public boolean XmlSettingChanged() {
		return xmlSettingChanged;
	}

	public DiscoveryProfile() {
		super(new Timer());
	}

	public DiscoveryProfile(Context context, ExecutionEvent event,File userfile,File globalfile,boolean debug) {
		super(new Timer(), event, context);
		
		String transName= getBuildEnvironment();
		
		String data = populateData();
		
		if (getSession() != null) {
			getSession().setEnvironment(transName);
			getSession().setStartTime(new Date(this.getTimer().getStartTime()));
			ProcessHelper.parseSessionPayLoad(data, getSession());
		}
	
		System.out.println("[INFO] Running From CI: " + this.isInJekins());
		System.out.println("[INFO] Build Environment: " + this.getBuildEnvironment());
		System.out.println("[INFO] Application Name: " + getSession().getAppName());
		
		mddaMain(userfile, globalfile,debug);
	}
	
	private void mddaMain(File userfile, File globalfile, boolean debug) {
		if (!this.getConfig().isGlobalSwitch()) {
			System.out.println("[INFO] MDDA pre-download turned off");
			return;
		} else {
			System.out.println("[INFO] MDDA pre-download turned on");
		}
		
		System.out.println("[INFO] MDDA pre-download start");
	
		ArtifactRepository lr = event.getSession().getLocalRepository();
		File localrepo = null;
		
		try {
			localrepo = new File(new URL(lr.getUrl()).toURI());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		String appName = null;
		appName = getSession().getAppName();
		
		if (appName == null) {
			System.out.println("[INFO] MDDA cannot find application name, MDDA exit.");
			return;
		}
		
		String pathmd5 = MD5Generator.generateMd5(System.getenv().get("PWD"));
		
		appName += "-" + pathmd5;
		
		fp = new FileProperties(appName); 
		
		File md5File = fp.getSettingXMLCacheMd5File();
		
		String fullString = FileUtils.readFile(globalfile);
		
		fullString += FileUtils.readFile(userfile);
	
		String md5 = MD5Generator.generateMd5(fullString);
		
		if (md5File.exists()) {
		
			String oldmd5 = FileUtils.readFile(md5File);

			System.out.println("[INFO] MDDA Previous Settings MD5: " + oldmd5);
			System.out.println("[INFO] MDDA Current  Settings MD5: " + md5);
			
			if(oldmd5.equals(md5)) {
				
				xmlSettingChanged = false;
				
				PreDownload.start(fp.getPreCacheListFile(), localrepo, debug);
			}
		} else {
			FileUtils.writeToFile(md5File, md5);
		}
		
	}

	private MavenProject getParentProject(final MavenProject project, final String groupId, final String artifactId) {
		if (project == null) {
			return null;
		}
		if (project.getGroupId().equals(groupId)
				&& project.getArtifactId().equals(artifactId)){
			return project;
		}
		
		if (project.getParent()==null){
			return null;
		}
			
		return getParentProject(project.getParent(), groupId, artifactId);
	}
	
	private String populateData() {
		StringBuilder data = new StringBuilder();
		data.append("git.url=").append(this.getGitRepoUrl());
		
		if(System.getenv("BUILD_URL") != null) {
            data.append(";jenkins.url=").append(System.getenv("BUILD_URL"));
            data.append(";git.branch=").append(System.getenv("GIT_BRANCH"));
        }
		
		try {
			data.append(";machine=").append(java.net.InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {}
		
		data.append(";uname=").append(System.getProperty("user.name"));
		
		if (event != null) {
			String mavenVersion = System.getProperty("maven.home");
			String javaVersion = System.getProperty("java.runtime.version");
			data.append(";maven.version=").append(mavenVersion).append(";java.version=").append(javaVersion);
		}	
		
		return data.toString();
	}
	
	private String exp2String(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}
	
	private String getExceptionMessages() {
		StringBuffer messageBuffer = new StringBuffer();
		
		List<Throwable> ts = this.event.getSession().getResult().getExceptions();
		
		for (int i = 0; i < ts.size(); i++) {
			messageBuffer.append(ts.get(i).getMessage());
			if (i != (ts.size() - 1)) {
				messageBuffer.append(DELIMI);
			}
		}
		return messageBuffer.toString();
	}
	
	private String getFullStackTrace() {
		StringBuffer stackTraceBuffer = new StringBuffer();
		List<Throwable> ts = this.event.getSession().getResult().getExceptions();
		
		for (int i = 0; i < ts.size(); i++) {
			stackTraceBuffer.append(exp2String(ts.get(i)));
			if (i != (ts.size() - 1)) {
				stackTraceBuffer.append(DELIMI);
			}
		}
		return stackTraceBuffer.toString();
	}

	@Override
	public void stop() {
		super.stop();
		
		String status = endTransaction();
		
		List<MavenProject> projects = this.event.getSession().getProjects();
		if (projects != null && projects.size() > 0) {
			MavenProject domainProject = getParentProject(projects.get(0), "com.ebay.raptor", "DomainParent");
			MavenProject raptorProject = getParentProject(projects.get(0), "com.ebay.raptor", "RaptorParent");
			if (raptorProject != null) {
				this.getSession().setRaptorVersion(raptorProject.getVersion());
			}
			if (domainProject != null) {
				this.getSession().setDomainVersion(domainProject.getVersion());
			}
		}

		this.getSession().setDuration(this.getElapsedTime());
		this.getSession().setStatus(status);
		
		if (this.event.getSession().getResult().hasExceptions()) {
			try {
				this.getSession().setExceptionMessage(this.getExceptionMessages());
				this.getSession().setFullStackTrace(this.getFullStackTrace());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (this.isInJekins()) {
			exportSession();
			return;
		}
		
		if (this.isInRIDE()) {
			new PostSessionClient().queue(getSession());
		}
	}
	
	private void exportSession() {
		SessionExporter exporter = new SessionExporter();
		exporter.process(this.getSession());
	}
}
