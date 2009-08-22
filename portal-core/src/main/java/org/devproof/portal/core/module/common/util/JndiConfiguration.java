/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.common.util;

import java.util.Properties;

import javax.naming.NamingException;

import org.devproof.portal.core.module.common.CommonConstants;
import org.springframework.jndi.JndiTemplate;

/**
 * @author Carsten Hufe
 */
public class JndiConfiguration {

	/**
	 * Resolves the hibernate connection isolation via JNDI, hibernate property:
	 * hibernate.connections.isolation
	 */
	public String resolveHibernateConnectionIsolation() {
		JndiTemplate jndi = new JndiTemplate();
		try {
			String dialect = (String) jndi.lookup(CommonConstants.JNDI_PROP_HIBERNATE_CONNECTION_ISOLATION);
			return dialect;
		} catch (NamingException e) {
			return CommonConstants.HIBERNATE_DEFAULT_CONNECTION_ISOLATION;
		}
	}

	/**
	 * Resolves the hibernate dialect via JNDI, hibernate property:
	 * hibernate.dialect
	 */
	public String resolveHibernateDialect() {
		JndiTemplate jndi = new JndiTemplate();
		try {
			String dialect = (String) jndi.lookup(CommonConstants.JNDI_PROP_HIBERNATE_DIALECT);
			return dialect;
		} catch (NamingException e) {
			return CommonConstants.HIBERNATE_DEFAULT_DIALECT;
		}
	}

	/**
	 * Resolves the hibernate hibernate.show_sql property
	 */
	public String resolveHibernateShowSql() {
		JndiTemplate jndi = new JndiTemplate();
		try {
			String dialect = (String) jndi.lookup(CommonConstants.JNDI_PROP_HIBERNATE_SHOW_SQL);
			return dialect;
		} catch (NamingException e) {
			return CommonConstants.HIBERNATE_DEFAULT_SHOW_SQL;
		}
	}

	/**
	 * Resolves the hibernate hibernate.format_sql property
	 */
	public String resolveHibernateFormatSql() {
		JndiTemplate jndi = new JndiTemplate();
		try {
			String dialect = (String) jndi.lookup(CommonConstants.JNDI_PROP_HIBERNATE_FORMAT_SQL);
			return dialect;
		} catch (NamingException e) {
			return CommonConstants.HIBERNATE_DEFAULT_FORMAT_SQL;
		}
	}

	/**
	 * Resolves the hibernate hibernate.hbm2ddl.auto property
	 */
	public String resolveHibernateHbm2ddlAuto() {
		JndiTemplate jndi = new JndiTemplate();
		try {
			String dialect = (String) jndi.lookup(CommonConstants.JNDI_PROP_HIBERNATE_HBM2DDL_AUTO);
			return dialect;
		} catch (NamingException e) {
			return CommonConstants.HIBERNATE_DEFAULT_HBM2DDL_AUTO;
		}
	}

	/**
	 * Returns all hibernate properties for the spring configuration
	 */
	public Properties resolveHibernateProperties() {
		Properties props = new Properties();
		props.put("hibernate.dialect", resolveHibernateDialect());
		props.put("hibernate.show_sql", resolveHibernateShowSql());
		props.put("hibernate.format_sql", resolveHibernateFormatSql());
		props.put("hibernate.hbm2ddl.auto", resolveHibernateHbm2ddlAuto());
		props.put("hibernate.connection.isolation", resolveHibernateConnectionIsolation());
		return props;
	}

	/**
	 * Returns the JNDI datasource name
	 */
	public String getDataSourceJndiName() {
		return CommonConstants.JNDI_DATASOURCE;
	}

	/**
	 * Returns the JNDI session mailer name
	 */
	public String getMailSessionJndiName() {
		return CommonConstants.JNDI_MAIL_SESSION;
	}
}
