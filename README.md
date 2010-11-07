[Devproof Portal](http://portal.devproof.org/)
==============================================

Project Page: [http://portal.devproof.org](http://portal.devproof.org)

What you need to build your own Devproof Portal
-----------------------------------------------

* Java 1.6 or higher (http://java.sun.com)
* Gradle 0.9-rc-2 or higher (http://www.gradle.org)
* Internet connection (for Maven artifacts and Tomcat download)

How to build your own Devproof Portal
-------------------------------------

In the main directory of the distribution, type the following to build the project:

`gradle build dist`

Oh, that's it! You will find the bundled version under *portal-bundle/build/dist/*.
Run the Apache Tomcat under 

for Windows `portal-bundle/build/dist/devproof-portal-1.1-rc1-SNAPSHOT-with-tomcat6/apache-tomcat-6.x.x/bin/startup.bat`

for Linux `portal-bundle/build/dist/devproof-portal-1.1-rc1-SNAPSHOT-with-tomcat6/apache-tomcat-6.x.x/bin/startup.sh`

Find more information on http://www.devproof.org/article/portal_getting_started