[Devproof Portal](http://portal.devproof.org/)
==============================================

Project Page: http://portal.devproof.org

What you need to build your own Devproof Portal
-----------------------------------------------

* Java 1.6 or higher (http://java.sun.com)
* Apache Maven 2.2.1 or higher (http://maven.apache.org)
* Internet connection (for Maven artifacts and Tomcat download)

How to build your own Devproof Portal
-------------------------------------

In the main directory of the distribution , type
the following to build the project:

`mvn clean install`

Oh, that's it! You will find the bundled version under *portal-bundle/target/*.
Run the Apache Tomcat under 

for Windows `portal-bundle/devproof-portal-1.0-rc4-with-tomcat6/apache-tomcat-6.x.x/bin/startup.bat`

or 

for Linux `portal-bundle/devproof-portal-1.0-rc4-with-tomcat6/apache-tomcat-6.x.x/bin/startup.sh`


Find more information on http://www.devproof.org/article/portal_getting_started