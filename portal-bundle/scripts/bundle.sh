#!/bin/sh

PORTAL_VERSION=1.0-rc2
cd ~
svn co https://devproof.svn.sourceforge.net/svnroot/devproof/trunk portal
cd portal/portal-build/
mvn clean install
cd ..
BUILD_VERSION=/var/build-versions/$PORTAL_VERSION-`date +"%F-%H%M%S"`/
mkdir $BUILD_VERSION
cp portal-webapp/target/devproof-portal-$PORTAL_VERSION.war $BUILD_VERSION
cp portal-bundle/target/devproof-portal-$PORTAL_VERSION-war.tar.gz $BUILD_VERSION
cp portal-bundle/target/devproof-portal-$PORTAL_VERSION-with-tomcat6.tar.gz $BUILD_VERSION
cd ..
rm -Rf portal