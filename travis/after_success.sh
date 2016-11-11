#!/bin/bash

# Get Commit Message
commit_message=$(git log --format=%B -n 1)
echo "Current commit detected: ${commit_message}"

# We build for several JDKs on Travis.
# Some actions, like analyzing the code (Coveralls) and uploading
# artifacts on a Maven repository, should only be made for one version.
 
# If the version is 1.8, then perform the following actions.
# 1. Upload artifacts to Sonatype.
# 2. Use -q option to only display Maven errors and warnings.
# 3. Use --settings to force the usage of our "settings.xml" file.
# 4. Notify Coveralls.
# 5. Deploy site

if [ $TRAVIS_REPO_SLUG == "dblock/waffle" ] && [ $TRAVIS_PULL_REQUEST == "false" ] && [ $TRAVIS_BRANCH == "master" ] && [[ "$commit_message" != *"[maven-release-plugin]"* ]]; then

  if [ $TRAVIS_JDK_VERSION == "oraclejdk8" ]; then
    # Deploy to sonatype
    ./mvnw deploy -DskipTests -q --settings ./travis/settings.xml
    echo -e "Successfully deployed SNAPSHOT artifacts to Sonatype under Travis job ${TRAVIS_JOB_NUMBER}"

    # Deploy to coveralls
    # Cannot run tests on linux
    # mvn clean test jacoco:report coveralls:report -q
    # echo -e "Successfully deployed Coveralls Report under Travis job ${TRAVIS_JOB_NUMBER}"

    # Deploy to site
    # Cannot currently run site this way
    # mvn site site:deploy -DskipTests -q
    # echo -e "Successfully deploy site under Travis job ${TRAVIS_JOB_NUMBER}"
  else
    echo "Java Version does not support additonal activity for travis CI"
  fi
else
  echo "Travis Pull Request: $TRAVIS_PULL_REQUEST"
  echo "Travis Branch: $TRAVIS_BRANCH"
  echo "Travis build skipped"
fi