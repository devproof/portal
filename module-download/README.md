Devproof module
===============

The SQLs are in */src/main/resources/sql* and must follow these naming convensions:

* create_tables_h2_yourmodule.sql for the H2 database create tables script.
* create_tables_mysql_yourmodule.sql for the MySQL create tables script.
* create_tables_oracle_yourmodule.sql for the Oracle create tables script.
* insert_yourmodule.sql for the inserts (will used for H2, MySQL and Oracle)

All SQL files will be bundled in the JAR file and later extracted to concatinate it. 
In the portal-webapp project the SQL files of all modules will be concatinated.
You will find the generated SQL files under */portal-bundle/target/sql/*

* install_devproof_h2.sql
* install_devproof_mysql.sql
* install_devproof_oracle.sql

The versions must be configured in the maven *pom.xml* file and in the *devproof-module.xml*

Project structure
-----------------------

package *org.devproof.portal.module.yourmodule*

* repository contains the generic repository/DAOinterfaces
* entity contains the JPA/Hibernate entities
* page contains the wicket pages
* panel contains subpanels
* query contains query objects for the generic QueryDataProvider
* service contains the business logic

*devproof-module.xml* is the spring application context and contains the module configuration.
The application context must be defined in META-INF/devproof.module