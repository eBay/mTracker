package com.ebay.build.profiler.profile;

import java.io.File;
import java.net.URL;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import com.ebay.build.cal.logging.CALLogger;
import com.ebay.build.profiler.util.GitUtil;
import com.ebay.build.profiler.util.Timer;

/**
 * @requiresDependencyResolution runtime
 * 
 */
public class Profile {
  
  protected long elapsedTime;
  protected Timer timer;
  
  protected static CALLogger calogger;
  
  protected ExecutionEvent event;
  protected String gitRepoUrl;
  
  protected Profile(Timer timer){
	  this.timer = timer;
  }
  
  protected Profile(Timer timer, ExecutionEvent event, Context context) {
    this.timer = timer; 
    this.event = event;
    DefaultPlexusContainer container = (DefaultPlexusContainer) context.getData().get("plexus");
    
    if (calogger == null){
        try {
    		calogger = container.lookup(CALLogger.class);
    	} catch (ComponentLookupException e) {
    		e.printStackTrace();
    	}
    }
    
    if (!calogger.isCalInitialized()){
    	initializeCAL();
    }
  }

private void initializeCAL() {
	String poolName = getAppName();
	URL calConfig = getClass().getClassLoader().getResource("cal.properties");
	calogger.initialize(calConfig, poolName);
}
  
  protected boolean isCalInitialized(){
	  return calogger.isCalInitialized();
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
				
		String appName = getCALPoolName(gitURL);

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
		
		System.out.println("Cal Pool Name : " + appName);
		return appName;
	}
}