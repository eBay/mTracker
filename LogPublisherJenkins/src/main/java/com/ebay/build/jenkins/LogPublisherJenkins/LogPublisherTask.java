package com.ebay.build.jenkins.LogPublisherJenkins;

import hudson.Extension;
import hudson.Launcher;
import hudson.init.Initializer;
import hudson.model.BuildListener;
import hudson.model.TopLevelItem;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.tasks.Ant;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class LogPublisherTask extends Recorder {

	private static final String LOG_PUBLISHER_JENKINS_PLUGIN_NAME = "Raptor Build Tracking Log Publisher";
	
	@Initializer(after=hudson.init.InitMilestone.JOB_LOADED)
	public static void init() {
		
		for (TopLevelItem item : Hudson.getInstance().getItems()) {
			AbstractProject<?, ?> project = (AbstractProject) item;
			
			if (!isPublisherExist(project)) {
				try {
					System.out.println("Register " + LOG_PUBLISHER_JENKINS_PLUGIN_NAME + "  to JOB " + project.getName());
					project.getPublishersList().add(new LogPublisherTask());
				} catch (IOException e) {
					System.err.println("Adding Publisher failed... ");
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean isPublisherExist(AbstractProject project) {
		List<Publisher> publishers = project.getPublishersList().toList();
		
		for (Publisher pub : publishers) {
			if (LOG_PUBLISHER_JENKINS_PLUGIN_NAME.equalsIgnoreCase(pub.getDescriptor().getDisplayName())) {
				return true;
			}
		}
		return false;
	}
	
	@DataBoundConstructor
	public LogPublisherTask() {
	}
	
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {
		
		VersionChecker vc = new VersionChecker(Hudson.getInstance().getRootDir());
		
		String properties = vc.getProperties();
		if (properties == null) {
			return false;
		}
		
		System.out.println("===== Hudson Root: " + Hudson.getInstance().getRootDir());
		System.out.println("===== properties: " + properties);
		
		listener.getLogger().println("===== Hudson Root: " + Hudson.getInstance().getRootDir() + "\n" + "===== properties: " + properties);
		
		listener.getLogger().println("**** " + Hudson.getInstance().getItems().size());
		
		try {
			publish(vc.getBuildFile(), build, launcher, listener, properties);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private void publish(File buildFile, AbstractBuild build, Launcher launcher, BuildListener listener, String properties) {
		if (buildFile.exists()) {
			Ant ant = new Ant("publish", "ANT171", "", buildFile.toString(), properties);
			try {
				ant.perform(build, launcher, listener);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }
	
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return LOG_PUBLISHER_JENKINS_PLUGIN_NAME;
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req,formData);
        }
    }
}
