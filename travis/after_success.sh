#!/bin/bash

# Get Project Repo
waffle_repo=$(git config --get remote.origin.url 2>&1)
echo "Repo detected: ${waffle_repo}"
 
# Get the Java version.
# Java 1.5 will give 15.
# Java 1.6 will give 16.
# Java 1.7 will give 17.
# Java 1.8 will give 18.
VER=`java -version 2>&1 | sed 's/java version "\(.*\)\.\(.*\)\..*"/\1\2/; 1q'`
echo "Java detected: ${VER}"

# We build for several JDKs on Travis.
# Some actions, like analyzing the code (Coveralls) and uploading
# artifacts on a Maven repository, should only be made for one version.
 
# If the version is 1.7, then perform the following actions.
# 1. Upload artifacts to Sonatype.
# 2. Use -q option to only display Maven errors and warnings.
# 3. Use --settings to force the usage of our "settings.xml" file.
# 4. Notify Coveralls.
# 5. Deploy site

if [ "$waffle_repo" == "https://github.com/dblock/waffle.git" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ] && [ "$TRAVIS_BRANCH" == "master" ]; then
  if [ $VER == "18" ]; then
    mvn clean deploy -q --settings ./travis/settings.xml
    echo -e "Successfully deployed SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"
	mvn clean test jacoco:report coveralls:report -q
	echo -e "Successfully deployed Coveralls Report under Travis job ${TRAVIS_JOB_NUMBER}"
	mvn site site:deploy -q
	echo -e "Successfully deploy site under Travis job ${TRAVIS_JOB_NUMBER}"
  fi
else
  echo "Travis build skipped"
fi