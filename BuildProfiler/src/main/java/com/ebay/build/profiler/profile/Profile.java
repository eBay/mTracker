package com.ebay.build.profiler.profile;

import com.ebay.build.profiler.util.Timer;

public class Profile {
  
  protected long elapsedTime;
  protected Timer timer;
    
  protected Profile(Timer timer) {
    this.timer = timer;    
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
}