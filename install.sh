#!/bin/bash

mkdir -p maven.build.tracking
cd maven.build.tracking
git clone https://[github]/mmao/maven-build-tracking > /dev/null
mvn clean install -DskipTests > /dev/null
rm $MAVEN_HOME/lib/ext/core.jar
rm $MAVEN_HOME/lib/ext/profiler.jar
mv core/target/core-*.jar $MAVEN_HOME/lib/ext
mv profiler/target/profiler-*.jar $MAVEN_HOME/lib/ext
wget https://[github]/mmao/maven-build-tracking/raw/master/publisher/bin/build.xml

