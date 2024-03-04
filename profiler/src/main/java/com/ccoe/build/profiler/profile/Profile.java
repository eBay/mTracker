/*
Copyright [2013-2014] eBay Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

*/

package com.ccoe.build.profiler.profile;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;

import com.ccoe.build.core.model.Session;
import com.ccoe.build.profiler.util.GitUtil;
import com.ccoe.build.profiler.util.Timer;
import com.ccoe.build.service.config.BuildServiceConfigBean;

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
  
  //private boolean skipMDDA = false;
  
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
    		machineName = InetAddress.getLocalHost().getCanonicalHostName();
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
	
	protected BuildServiceConfigBean getConfig() {
		if (this.context != null) {
			return (BuildServiceConfigBean) this.context.getData().get(BuildServiceConfigBean.class.toString());
		}
		return new BuildServiceConfigBean();
	}
	
	protected boolean isInJenkins() {
		return "CI".equalsIgnoreCase(getBuildEnvironment());
	}
	
	protected boolean isInIDE() {
		String env = getBuildEnvironment(); 
		return env!= null && env.contains("IDE");
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
		if (userHome != null && userHome.contains("/var/lib/jenkins")) {
			System.out.println("[INFO] user.home: " + System.getenv("user.home"));
			return true;
		}
		return false;
	}
}
