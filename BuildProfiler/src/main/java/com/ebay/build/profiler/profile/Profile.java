package com.ebay.build.profiler.profile;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.maven.eventspy.EventSpy.Context;
import org.apache.maven.execution.ExecutionEvent;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import com.ebay.build.cal.logging.CALLogger;
import com.ebay.build.cal.model.Machine;
import com.ebay.build.cal.model.Pool;
import com.ebay.build.cal.model.Session;
import com.ebay.build.profiler.util.GitUtil;
import com.ebay.build.profiler.util.Timer;
import com.ebay.kernel.calwrapper.CalTransaction;

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
  
  private Context context;
  
  protected Profile(Timer timer){
	  this.timer = timer;
  }
  
  protected Profile(Timer timer, ExecutionEvent event, Context c) {
    this.timer = timer; 
    this.event = event;
    this.context = c;
    
    if (context != null) {
    	DefaultPlexusContainer container = (DefaultPlexusContainer) context.getData().get("plexus");

    	if (calogger == null) {
    		try {
    			calogger = container.lookup(CALLogger.class);
    		} catch (ComponentLookupException e) {
    			e.printStackTrace();
    		}
    	}

    	if (isCALEnabled() && !calogger.isCalInitialized()) {
    		System.out.println("[INFO] Initializing CAL...");
    		initializeCAL();
    	}
    }
  }

private void initializeCAL() {
	String poolName = getAppName();
	String machineName = "N/A";
	try {
		machineName = InetAddress.getLocalHost().getHostName();
	} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	URL calConfig = getClass().getClassLoader().getResource("cal.properties");
	calogger.initialize(calConfig, poolName, machineName);
	
	if (getSession().getPool() == null) {
		Pool pool = new Pool();
		pool.setName(poolName);
		Machine machine = new Machine();
		machine.setName(machineName);
		pool.setMachine(machine);
		getSession().setPool(pool);
    }
}
  
  protected boolean isCalInitialized(){
	  if (!isCALEnabled()) {
		  return false;
	  }
	  return calogger != null && calogger.isCalInitialized();
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
		
		String appName = "UNKNON";
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
		System.out.println("Cal Pool Name : " + appName);
				
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
	
	protected boolean isCALEnabled() {
		String value = System.getProperty("cal.logging");
		if (value == null) {
			return true;
		}
		return !("off".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value));
	}
	
	protected boolean isInJekins() {
		return "CI".equalsIgnoreCase(context.getData().get("build.env").toString());
	}
	
	protected String endTransaction(CalTransaction transaction) {
		String status = "0";
		Exception exception = null;
		
		if(event != null && event.getSession().getResult().getExceptions().size() > 0) {
			status = "1";
			exception = event.getException();
		}
		
		if (!isInJekins() && transaction != null) {
			if (exception != null) {
				calogger.endCALTransaction(transaction, status, exception);
			} else {
				calogger.endCALTransaction(transaction, status);
			}
		}
		return status;
	}
}