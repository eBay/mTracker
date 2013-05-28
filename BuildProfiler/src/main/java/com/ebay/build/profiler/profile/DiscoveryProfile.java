package com.ebay.build.profiler.profile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;

import com.ebay.build.cal.processors.SessionExporter;
import com.ebay.build.profiler.readers.ProcessHelper;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;


public class DiscoveryProfile extends Profile {
	
	private static final String DELIMI = "----------";
	
	private CalTransaction discoveryTransaction;
	
	public DiscoveryProfile() {
		super(new Timer());
		
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction("DEV", "Environment", "data");
		}
	}

	public DiscoveryProfile(Context context, ExecutionEvent event) {
		super(new Timer(), event, context);
		
		String transName= getBuildEnvironment();
		
		String data = populateData();
		
		if (this.isInJekins()) {
			getSession().setEnvironment(transName);
			getSession().setStartTime(new Date(this.getTimer().getStartTime()));
			ProcessHelper.parseSessionPayLoad(data, getSession());
		}
	
		if(isCalInitialized()) {
			discoveryTransaction = calogger.startCALTransaction(transName, "Environment",  data);
		}
		
		System.out.println("[INFO] CAL logging Enabled: " + this.isCALEnabled());
		System.out.println("[INFO] Running From CI: " + this.isInJekins());
		System.out.println("[INFO] Build Environment: " + this.getBuildEnvironment());
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
		
		String status = endTransaction(discoveryTransaction);
		
		if (this.isInJekins()) {
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
			
			exportSession();
			return;
		}
		
		if (isCalInitialized()) {
			try {
				System.out.println("Stopping CAL Service...");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			calogger.destroy();
		}
	}
	
//	private void importToDB() {
//		try {
//			LoaderProcessor processor = new LoaderProcessor(event.getSession().getSystemProperties().getProperty("maven.home"));
//			System.out.println("[INFO] Storing Session into DB...");
//			processor.process(getSession());
//			System.out.println("[INFO] Session store completed!");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	private void exportSession() {
		SessionExporter exporter = new SessionExporter();
		exporter.process(this.getSession());
	}
}
