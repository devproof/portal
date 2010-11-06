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

import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.app.PortalContextLoaderListener;
import org.devproof.portal.core.module.common.CommonConstants;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.mail.Session;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.Properties;

/**
 * @author Carsten Hufe
 */
public class MockContextLoader implements ContextLoader {

    @Override
    public String[] processLocations(Class<?> clazz, String... locations) {
        return locations;
    }

    @Override
    public ConfigurableApplicationContext loadContext(String... locations) throws Exception {
		registerResource(CommonConstants.JNDI_MAIL_SESSION, Session.getDefaultInstance(new Properties()));
		registerResource(CommonConstants.JNDI_PROP_EMAIL_DISABLED, "true");
        registerResource(CommonConstants.JNDI_PROP_HIBERNATE_DIALECT, "org.hibernate.dialect.HSQLDialect");
		registerResource(CommonConstants.JNDI_PROP_HIBERNATE_SECOND_LEVEL_CACHE, "false");
		registerResource(CommonConstants.JNDI_PROP_HIBERNATE_QUERY_CACHE, "false");
        ConfigurableWebApplicationContext context = new XmlWebApplicationContext();
        MockServletContext servletContext = new MockServletContext("") {
            @Override
            public String getRealPath(String arg0) {
                return System.getProperty("java.io.tmpdir");
            }
        };
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
        context.setServletContext(servletContext);
        context.setConfigLocations(PortalContextLoaderListener.locateConfigLocations(locations));
        context.refresh();
        context.registerShutdownHook();
        return context;
    }

    private static void registerResource(String jndiName, Object jndiObj) {
		try {
			new org.mortbay.jetty.plus.naming.Resource(jndiName, jndiObj);
		} catch (NamingException e) {
			throw new UnhandledException(e);
		}
	}
}
