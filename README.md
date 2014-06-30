Maven Build Tracking
===================


Maven Build Tracking is a system used to track the Maven builds in various environments (IDE, CLI, CI etc.), 
load the data into central DB for analyzing performance/reliability metrics.
It can be easily integrated with reporting system.
It is low-footprint, low-latency, self-upgrade, easy to maintain.

## Learning about Maven Build Tracking

Logically, Maven Build Tracking is consist of three marjor components. 

1. Profiler 
2. LogPublisher
3. PostProcessor

*Profiler* basically is the Maven extensions, we put two jars into $MAVEN_HOME/lib/ext.
It loaded by Maven core and listen for the Maven events (session start/stop, project start/stop, plugin start/stop etc.), 
at the end of Maven session, the Profiler dump a log file which include all the tracked data.

```
@Named
@Singleton
public class MavenLifecycleProfiler extends AbstractEventSpy {

    @Override
    public void init(Context context) throws Exception {}
    
    @Override
    public void onEvent(Object event) throws Exception {}

    @Override
    public void close() throws Exception {}

}
```

*LogPublisher* is a single executable jar which hosted on the machine which has the logs produced by Profiler. 
It scan the log direcotry for unprocessed log files, and load the log files, transform/process them, and finally
import them into the central DB. LogPublisher can self-upgrade, Imaging if you deploy it in a 500+ Jenkins CI machines,
what a nightmare if you have to manually install them when you have a new version released.


*PostProcessor* is used to handle some long-running tasks which are not suitable in Profiler which is aimed to 
be low-footprint, low-latency. Those long-running tasks is not suitble for Profiler, if so, it will make the build longer,
it also possess more build machine's compute resource. It's not suitable in LogPublisher neither, because it's also in build machines.
PostProcessor is consist of some RESTful services and some scheduled Quartz tasks.

Following is a list of the information MBT tracked

* Session status (Success/Fail)
* Session duration
* Session pure build duration
* Session maven downloading duration
* Project duration
* Phase duration
* Plugin duration
* Environment (CLI/CI/RIDE)
*	Machine Name FQDN
*	User Name
*	Git URL
*	Jenkins URL
*	Maven version
*	Java version
*	Error Category
*	Error Code
*	Fullstack Trace

## Overview

### The log based architecture

![Image of architecture](https://github.scm.corp.ebay.com/mmao/maven-time-tracking/raw/master/docs/images/arch.png)


## Setting Up

### Requirements

* Maven 3.0.5+
* JDK 1.6+
* ANT 1.8+

### Installation

1. [Download](https://github.scm.corp.ebay.com/mmao/maven-time-tracking/tree/master/downloads) the core.jar and profiler.jar, copy these two jars into $MAVEN_HOME/lib/ext
2. mkdir /var/lib/jenkins/maven.build.tracking
3. cd /var/lib/jenkins/maven.build.tracking
4. wget https://github.scm.corp.ebay.com/mmao/maven-time-tracking/raw/master/publisher/bin/build.xml
5. setup cron job

```
# Name: maven-build-tracking LogPublish
*/5 * * * * cd /var/lib/jenkins/maven.build.tracking; /usr/bin/ant -logger org.apache.tools.ant.listener.MailLogger > crontab-publish.log 2>&1
# Name: maven-build-tracking SelfUpgrade
0 * * * * cd /var/lib/jenkins/maven.build.tracking; /usr/bin/ant upgrade -logger org.apache.tools.ant.listener.MailLogger > crontab-upgrade.log 2>&1
```

### Build From Source

1. git clone https://github.scm.corp.ebay.com/mmao/maven-time-tracking
2. mvn clean install
