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
package org.devproof.portal.test;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.page.LoginPage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;

/**
 * Class contains stuff for testing convience
 * 
 * @author Carsten Hufe
 * 
 */
public class PortalTestUtil {
	public final static String SQL_FILES[] = { "create_tables_hsql_core.sql", "insert_core.sql" };
	private static MockServletContext sandbox = null;

	/**
	 * Returns the content of a file as String
	 */
	public static String getFileContent(final ApplicationContext context, final String file) throws IOException {
		Resource r = context.getResource("classpath:/sql/" + file);
		final InputStream is = r.getInputStream();
		final byte buffer[] = new byte[is.available()];
		is.read(buffer);
		is.close();
		return new String(buffer);
	}

	/**
	 * Creates the data structure with the sql files Uses the default from
	 * spring-test-datasource.xml
	 */
	public static void createDataStructure(final List<String> files) throws SQLException, IOException {
		final ClassPathXmlApplicationContext dsAppContext = new ClassPathXmlApplicationContext("classpath:/devproof-test-datasource.xml");
		final DataSource ds = (DataSource) dsAppContext.getBean("dataSource");
		Connection connection = ds.getConnection();
		Statement stmt = connection.createStatement();
		// clean db
		stmt.execute("SHUTDOWN;");
		stmt.close();
		connection.close();
		connection = ds.getConnection();
		for (final String file : files) {
			stmt = connection.createStatement();
			stmt.addBatch(PortalTestUtil.getFileContent(dsAppContext, file));
			stmt.executeBatch();
			stmt.close();
		}
		connection.close();
		dsAppContext.close();
	}

	/**
	 * Creates the data structure with the sql files Uses the default from
	 * spring-test-datasource.xml
	 */
	public static void createDefaultDataStructure(final String[] sqlFiles) throws SQLException, IOException {
		List<String> files = new ArrayList<String>();
		for (String file : SQL_FILES) {
			files.add(file);
		}
		if (sqlFiles != null) {
			for (String file : sqlFiles) {
				files.add(file);
			}
		}
		createDataStructure(files);
	}

	/**
	 * Returns the wicket tester instance for PortalApplication
	 */
	public static WicketTester createWicketTesterWithSpring() {
		if (sandbox == null) {
			sandbox = new MockServletContext("") {
				// this is for the theme page test
				@Override
				public String getRealPath(final String arg0) {
					return System.getProperty("java.io.tmpdir");
				}
			};
			sandbox.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath:/devproof-test.xml\nclasspath*:/**/devproof-module.xml");
			final ContextLoader contextLoader = new ContextLoader();
			contextLoader.initWebApplicationContext(sandbox);
		}
		final PortalApplication app = new PortalApplication() {
			@Override
			public ServletContext getServletContext() {
				return sandbox;
			}
		};

		// Workaround for bug in WicketTester, mounted url does not work
		// with stateless form
		app.unmount("/login");

		return new WicketTester(app);
	}

	/**
	 * Create database and spring context
	 */
	public static WicketTester createWicketTesterWithSpringAndDatabase(final String... sqlFiles) throws SQLException, IOException {
		createDefaultDataStructure(sqlFiles);
		return createWicketTesterWithSpring();
	}

	public static void destroy(final WicketTester tester) {
		tester.destroy();
	}

	/**
	 * Login the default admin user
	 */
	public static void loginDefaultAdminUser(final WicketTester tester) throws UserNotConfirmedException {
		tester.startPage(LoginPage.class);
		final FormTester form = tester.newFormTester("loginForm");
		form.setValue("username", "admin");
		form.setValue("password", "12345");
		form.submit();
	}
}
