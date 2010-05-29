Devproof module
===============

The SQLs are in /src/main/resources/sql and must follow these naming convensions:
* create_tables_hsql_<yourmodule>.sql for the HSQL create tables script.
* create_tables_mysql_<yourmodule>.sql for the MySQL create tables script.
* insert_<yourmodule>.sql for the inserts (will used for HSQL and MySQL)

All SQL files will be bundled in the JAR file and later extracted to concatinate it. 
In the portal-webapp project the SQL files of all modules will be concatinated.
You will find it unter /portal-webapp/target/sql/install_devproof_mysql.sql.

The versions must be configured in the maven pom.xml file and in the devproof-module.xml

Project structure:

package org.devproof.portal.module.<yourmodule>
* dao contains the generic DAO interfaces
* entity contains the JPA/Hibernate entities
* page contains the wicket pages
* panel contains subpanels
* query contains query objects for the generic QueryDataProvider
* service contains the business logic

devproof-module.xml is the spring application context 
and contains the module configuration.