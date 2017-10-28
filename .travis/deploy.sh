#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    echo "Starting deployment to Sonatype"
    mvn deploy -P sign,build-extras --settings .travis/mvnsettings.xml
    echo "Deployment to Sonatype has finished"
fi