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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.app.PortalApplication;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.user.page.LoginPage;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;

import javax.mail.Session;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * Class contains stuff for testing convience
 *
 * @author Carsten Hufe
 */
public class PortalTestUtil {
    public final static String SQL_FILES[] = {"create_tables_hsql_core.sql", "insert_core.sql"};
    private static MockServletContext sandbox = null;

    /**
     * Returns the content of a file as String
     */
    public static String getFileContent(String file) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        final Resource r;
        if (file.startsWith("file:/")) {
            r = resourceLoader.getResource(file);
        } else {
            r = resourceLoader.getResource("classpath:/sql/create/" + file);
        }
        InputStream is = r.getInputStream();
        byte buffer[] = new byte[is.available()];
        is.read(buffer);
        is.close();
        String str = new String(buffer);
        str = StringUtils.remove(str, "\\n\n");
        return StringUtils.remove(str, "\\n");
    }

    /**
     * Creates the data structure with the sql files Uses the default from
     * spring-test-datasource.xml
     */
    public static void createDataStructure(List<String> files) throws SQLException, IOException {
        try {
            DataSource ds = (DataSource) new JndiTemplate().lookup(CommonConstants.JNDI_DATASOURCE);
            Connection connection = ds.getConnection();
            Statement stmt = connection.createStatement();
            // clean db
            stmt.execute("SHUTDOWN;");
            stmt.close();
            connection.close();
            connection = ds.getConnection();
            for (String file : files) {
                stmt = connection.createStatement();
                stmt.addBatch(PortalTestUtil.getFileContent(file));
                stmt.executeBatch();
                stmt.close();
            }
            connection.close();
        } catch (NamingException e) {
            throw new UnhandledException(e);
        }
    }

    /**
     * Creates the data structure with the sql files Uses the default from
     * spring-test-datasource.xml
     */
    public static void createDefaultDataStructure(String[] sqlFiles) throws SQLException, IOException {
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
    public static WicketTester createWicketTesterWithSpring(String spring) {
        MockServletContext sandbox = getSandbox(spring);
        PortalApplication app = new TestPortalApplication(sandbox);

        // Workaround for bug in WicketTester, mounted url does not work
        // with stateless form
        app.unmount("/login");
        return new WicketTester(app);
    }


    private static MockServletContext getSandbox(String spring) {
        if (sandbox == null) {
            sandbox = new MockServletContext("") {
                @Override
                public String getContextPath() {
                    return null;
                }

                // this is for the theme page test
                @Override
                public String getRealPath(String arg0) {
                    return System.getProperty("java.io.tmpdir");
                }
            };
            sandbox.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath:/devproof-portal-core.xml\n" + spring);
            ContextLoader contextLoader = new ContextLoader();
            contextLoader.initWebApplicationContext(sandbox);
        }
        return sandbox;
    }

    /**
     * Create database and spring context
     */
    public static WicketTester createWicketTesterWithSpringAndDatabase(String... sqlFiles) throws SQLException, IOException {
        return createWicketTesterWithCustomSpringAndDatabase("classpath*:/**/devproof-module.xml", sqlFiles);
    }

    public static WicketTester createWicketTesterWithCustomSpringAndDatabase(String spring, String... sqlFiles) throws SQLException, IOException {
        registerJndiBindings();
        createDefaultDataStructure(sqlFiles);
        return createWicketTesterWithSpring(spring);
    }

    /**
     * Registers JNDI bindings
     */
    public static void registerJndiBindings() {
        SimpleDriverDataSource datasource = new SimpleDriverDataSource();
        datasource.setUrl("jdbc:hsqldb:mem:testdb");
        datasource.setUsername("sa");
        datasource.setPassword("");
        datasource.setDriverClass(org.hsqldb.jdbcDriver.class);
        registerResource(CommonConstants.JNDI_DATASOURCE, datasource);
        registerResource(CommonConstants.JNDI_MAIL_SESSION, Session.getDefaultInstance(new Properties()));
        registerResource(CommonConstants.JNDI_PROP_HIBERNATE_DIALECT, "org.hibernate.dialect.HSQLDialect");
        registerResource(CommonConstants.JNDI_PROP_HIBERNATE_SECOND_LEVEL_CACHE, "false");
        registerResource(CommonConstants.JNDI_PROP_HIBERNATE_QUERY_CACHE, "false");
        registerResource(CommonConstants.JNDI_PROP_EMAIL_DISABLED, "true");
    }

    public static void registerResource(String jndiName, Object jndiObj) {
        try {
            new org.mortbay.jetty.plus.naming.Resource(jndiName, jndiObj);
        } catch (NamingException e) {
            throw new UnhandledException(e);
        }
    }

    public static void destroy(WicketTester tester) {
        tester.destroy();
    }

    /**
     * Login the default admin user
     */
    public static void loginDefaultAdminUser(WicketTester tester) {
        tester.startPage(LoginPage.class);
        FormTester form = tester.newFormTester("loginForm");
        form.setValue("username", "admin");
        form.setValue("password", "admin");
        form.submit();
    }

    public static String[] getMessage(String key, Component component) {
        return new String[]{new StringResourceModel(key, component, null).getString()};
    }

    public static String[] getMessage(String key, Class<?> clazz) {
        Properties prop = new Properties();
        try {
            prop.load(clazz.getResourceAsStream(clazz.getSimpleName() + ".properties"));
        } catch (IOException e) {
            // do nothing
        }
        return new String[]{prop.getProperty(key)};
    }

    public static void callOnBeginRequest() {
        try {
            Method method = RequestCycle.class.getDeclaredMethod("onBeginRequest", (Class<?>[]) null);
            method.setAccessible(true);
            method.invoke(RequestCycle.get(), (Object[]) null);
        } catch (Exception e) {
            throw new UnhandledException(e);
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) ContextLoader.getCurrentWebApplicationContext().getBean(beanName);
    }

    private static class TestPortalApplication extends PortalApplication {
        private final MockServletContext sandbox;

        public TestPortalApplication(MockServletContext sandbox) {
            this.sandbox = sandbox;
        }

        @Override
        public ServletContext getServletContext() {
            return sandbox;
        }

        @Override
        public org.apache.wicket.Session newSession(Request request, Response response) {
            org.apache.wicket.Session session = super.newSession(request, response);
            session.setLocale(Locale.ENGLISH);
            return session;
        }
    }
}
