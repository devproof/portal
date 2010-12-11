/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.test;

import com.mysql.jdbc.Driver;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.wicket.protocol.http.WicketFilter;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.app.PortalContextLoaderListener;
import org.devproof.portal.core.module.common.CommonConstants;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.plus.naming.Resource;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.management.MBeanContainer;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * Test jetty start class for development
 *
 * @author Carsten Hufe
 */
public class JettyStart {

    public static void main(final String[] args) throws Exception {
        if (args.length != 5) {
            System.out.println("JettyStart <DbUser/Pass/Schema> <httpport> <smtphost> <smtpuser> <smtppass>");
            return;
        }
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        // Set some timeout options to make debugging easier.
        connector.setMaxIdleTime(1000 * 60 * 60);
        connector.setSoLingerTime(-1);
        connector.setPort(Integer.valueOf(args[1]));
        server.setConnectors(new Connector[]{connector});

        WebAppContext bb = new WebAppContext();
        bb.setServer(server);
        bb.setContextPath("/");
        bb.setWar(System.getProperty("java.io.tmpdir"));
        bb.addEventListener(new PortalContextLoaderListener());
        FilterHolder filter = new FilterHolder();
        filter.setInitParameter("applicationClassName", PortalApplication.class.getName());
        // servlet.setInitParameter("configuration", "deployment");
        filter.setClassName(WicketFilter.class.getName());
        filter.setName(WicketFilter.class.getName());
        bb.addFilter(filter, "/*", 1);
        server.addHandler(bb);

        BasicDataSource datasource = new BasicDataSource();
        datasource.setUrl("jdbc:mysql://localhost/" + args[0]);
        datasource.setUsername(args[0]);
        datasource.setPassword(args[0]);
        datasource.setDriverClassName(Driver.class.getName());
        new Resource(CommonConstants.JNDI_DATASOURCE, datasource);

        Properties props = System.getProperties();
        props.put("mail.smtp.host", args[2]);
        props.put("mail.smtp.auth", "true");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(args[3], args[4]);
            }
        };

        Session mailSession = Session.getDefaultInstance(props, auth);
        new Resource(CommonConstants.JNDI_MAIL_SESSION, mailSession);
        new Resource(CommonConstants.JNDI_PROP_EMAIL_DISABLED, "true");
        // START JMX SERVER
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
        server.getContainer().addEventListener(mBeanContainer);
        mBeanContainer.start();
        try {
            System.out.println(">>> STARTING DEVPROOF PORTAL, PRESS ANY KEY TO STOP");
            server.start();
            while (System.in.available() == 0) {
                Thread.sleep(5000);
            }
            server.stop();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(100);
        }
    }
}
