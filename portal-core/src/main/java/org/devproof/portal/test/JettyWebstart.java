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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Session;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.wicket.protocol.http.WicketFilter;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.module.common.CommonConstants;
import org.hibernate.dialect.HSQLDialect;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.plus.naming.Resource;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.web.context.ContextLoaderListener;

/**
 * Test jetty start class for Java Webstart
 * 
 * @author Carsten Hufe
 */
public class JettyWebstart {

	public static void main(final String[] args) throws Exception {
		Server server = new Server();
		SocketConnector connector = new SocketConnector();
		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8888);
		server.setConnectors(new Connector[] { connector });
		WebAppContext bb = new WebAppContext();
		bb.setServer(server);
		bb.setContextPath("/");
		bb.setWar(System.getProperty("java.io.tmpdir"));
		bb.addEventListener(new ContextLoaderListener());
		Map<String, String> initParams = new HashMap<String, String>();
		initParams.put("contextConfigLocation", "classpath:/devproof-portal.xml");
		bb.setInitParams(initParams);
		FilterHolder servlet = new FilterHolder();
		servlet.setInitParameter("applicationClassName", PortalApplication.class.getName());
		servlet.setInitParameter("configuration", "deployment");
		servlet.setClassName(WicketFilter.class.getName());
		servlet.setName(WicketFilter.class.getName());
		bb.addFilter(servlet, "/*", 0);
		server.addHandler(bb);

		SimpleDriverDataSource datasource = new SimpleDriverDataSource();
		datasource.setUrl("jdbc:hsqldb:mem:testdb");
		datasource.setUsername("sa");
		datasource.setPassword("");
		datasource.setDriverClass(org.hsqldb.jdbcDriver.class);
		new Resource(CommonConstants.JNDI_DATASOURCE, datasource);
		Properties props = System.getProperties();
		Session mailSession = Session.getDefaultInstance(props);
		new Resource(CommonConstants.JNDI_MAIL_SESSION, mailSession);
		new Resource(CommonConstants.JNDI_PROP_HIBERNATE_DIALECT, HSQLDialect.class.getName());
		PortalTestUtil.createDataStructure(Arrays.asList("file:///E:/Workspaces/devproof/portal-webapp/target/sql/install_devproof_hsql.sql"));
		// PortalTestUtil.createDataStructure(Arrays.asList("install_devproof_hsql.sql"));
		try {
			System.out.println(">>> STARTING DEVPROOF PORTAL, PRESS ANY KEY TO STOP");
			server.start();
			// while (true) {
			// Thread.sleep(5000);
			// }
			// server.stop();
			// server.join();
			JFrame frame = new JFrame("Hallo1");
			frame.show();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (Exception e) {
			e.printStackTrace();
			JFrame frame = new JFrame("Hallo1");
			frame.add(new JTextArea(e.toString()));
			frame.show();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// System.exit(100);
		}
	}
}
