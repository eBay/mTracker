package com.ebay.build.profiler.profile;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ebay.build.profiler.model.Session;
import com.ebay.build.profiler.util.GitUtil;
import com.ebay.build.profiler.util.Timer;

/**
 * @requiresDependencyResolution runtime
 * 
 */
public class Profile {
  
  protected long elapsedTime;
  protected Timer timer;
  
  protected ExecutionEvent event;
  protected String gitRepoUrl;
  
  private Context context;
  
  private String environment;
  
  protected Profile(Timer timer){
	  this.timer = timer;
  }
  
  protected Profile(Timer timer, ExecutionEvent event, Context c) {
    this.timer = timer; 
    this.event = event;
    this.context = c;
    
    if (context != null) {
    	
    	String poolName = getAppName();
    	String machineName = "N/A";
    	try {
    		machineName = InetAddress.getLocalHost().getHostName();
    	} catch (UnknownHostException e) {
    		e.printStackTrace();
    	}

    	if (getSession().getAppName() == null) {
    		getSession().setAppName(poolName);
    	}
    	if (getSession().getMachineName() == null) {
    		getSession().setMachineName(machineName);
    	}
    }
  }

  protected boolean isCalInitialized(){
	  return false;
  }
    
  public void stop() {
    timer.stop();
  }
  
  public void setElapsedTime(long elapsedTime) {
    this.elapsedTime = elapsedTime;
  }
  
  public long getElapsedTime() {
    if(elapsedTime != 0) {
      return elapsedTime;
    }
    return timer.getTime();
  }
  
  public Timer getTimer() {
	  return this.timer;
  }
  
  protected String getGitRepoUrl() {
	  return this.gitRepoUrl == null ? "N/A" : this.gitRepoUrl;
  }
  
  private String getAppName() {
		File gitMeta = GitUtil.findGitRepository(new File(event.getSession().getExecutionRootDirectory()));
		String gitURL = "";
		
		if(gitMeta != null && gitMeta.exists()) {
			File gitConfig = new File(new File(gitMeta,".git"), "config");
			gitURL =  GitUtil.getRepoName(gitConfig);
			if(gitURL != null) {
				this.gitRepoUrl = gitURL;
			}
		}
		
		String appName = "UNKNOWN";
		File currentDir = new File(event.getSession().getExecutionRootDirectory());
		
		if(gitMeta != null && gitMeta.exists()) {
			File gitConfig = new File(new File(gitMeta,".git"), "config");
			gitURL =  GitUtil.getRepoName(gitConfig);
			
			if (null == gitURL) {
				appName = gitMeta.getName();
			} else {
				appName = getCALPoolName(gitURL);
			}
		} else {
			if (null != currentDir && currentDir.exists()) {
				appName = currentDir.getName();
			}
		}
		
		System.out.println("Application Name: " + appName);
				
		return appName;
	}
	
	private String getCALPoolName(String gitURL) {
		String appName = "UNKNOWN";
		
		if (gitURL == null){
			return appName;
		}
		
		if ( gitURL.startsWith("https:") || gitURL.startsWith("git@") || gitURL.startsWith("git:") ) {
			appName = gitURL.substring(gitURL.lastIndexOf("/")+1);
			if(appName.endsWith(".git")) {
				appName = appName.substring(0, appName.lastIndexOf("."));
			}
		}
		
		return appName;
	}
	
	protected Session getSession() {
		if (this.context != null) {
			return (Session) this.context.getData().get(Session.class.toString());
		}
		return null;
	}
	
	protected boolean isInJekins() {
		return "CI".equalsIgnoreCase(getBuildEnvironment());
	}
	
	protected boolean isInRIDE() {
		return "RIDE".equalsIgnoreCase(getBuildEnvironment());
	}
	
	protected String endTransaction() {
		String status = "0";
		
		if(event != null && event.getSession().getResult().getExceptions().size() > 0) {
			status = "1";
		}
		
		return status;
	}
	
	protected String getBuildEnvironment() {
		if (null != environment) {
			return environment;
		}
		
		environment = System.getProperty("build.env");
		if (null != environment) {
			return environment;
		}
		
		String eclipseEnv = System.getProperty("eclipse.p2.profile");
		
		if(isInCIEnv()) {
			environment = "CI";
		} else if (eclipseEnv != null) {
			environment = eclipseEnv;
		} else {
			environment = "DEV";
		}
		
		return environment;
	}
	
	private boolean isInCIEnv() {
		if (System.getenv("BUILD_URL") != null
				|| System.getenv("JENKINS_HOME") != null 
				|| System.getenv("HUDSON_HOME") != null) {
			System.out.println("[INFO] BUILD_URL: " + System.getenv("BUILD_URL"));
			System.out.println("[INFO] JENKINS_HOME: " + System.getenv("JENKINS_HOME"));
			System.out.println("[INFO] HUDSON_HOME: " + System.getenv("HUDSON_HOME"));
			return true;
		}
		String userHome = System.getenv("user.home");
		if (userHome.contains("/var/lib/jenkins")) {
			System.out.println("[INFO] user.home: " + System.getenv("user.home"));
			return true;
		}
		return false;
	}
}