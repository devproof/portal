#!/bin/sh

PORTAL_VERSION=1.0-rc2
cd ~
svn co https://devproof.svn.sourceforge.net/svnroot/devproof/trunk portal
cd portal/portal-build
mvn install
cd ..
cp portal-webapp/target/devproof-portal-$PORTAL_VERSION.war /opt/build-wars/devproof-portal-$PORTAL_VERSION-`date +"%F-%H%M%S"`.war
cd ..
mkdir pkgportal
cd pkgportal
mkdir sources
mkdir sql
find ../portal/ -name devproof*sources.jar | xargs cp --target-directory=sources/
cp ../portal/portal-webapp/target/devproof-portal-$PORTAL_VERSION.war .
cp -R ../portal/portal-webapp/licenses/ .
cp ../portal/portal-webapp/README .
cp ../portal/portal-webapp/CHANGES .
find ../portal/portal-webapp/ -name install*.sql | xargs cp --target-directory=sql/
cp -R ../portal/portal-webapp/config/ .
find . -name .svn | xargs rm -Rf
tar cfvz ../devproof-portal-$PORTAL_VERSION.tar.gz .
cd ..
