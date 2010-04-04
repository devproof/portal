/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
package org.devproof.portal.core.app;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Carsten Hufe
 */
public class PortalModuleImporter implements ServletContextAware, ApplicationContextAware, InitializingBean {

	private static final String DEVPROOF_MODULE_XML = "devproof-module.xml";

	private ServletContext servletContext;
	private ConfigurableWebApplicationContext applicationContext;

	@Override
	public void afterPropertiesSet() throws Exception {
		List<String> modules = new ArrayList<String>();
		modules.add("classpath:/devproof-portal-core.xml");

		// Prod modules
		@SuppressWarnings("unchecked")
		Set<String> libs = servletContext.getResourcePaths("/WEB-INF/lib");
		for (String lib : libs) {
			URL url = servletContext.getResource(lib);
			JarFile file = new JarFile(url.getFile());
			Enumeration<JarEntry> entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				if (jarEntry.getName().endsWith(DEVPROOF_MODULE_XML)) {
					modules.add("classpath:/" + jarEntry.getName());
				}
			}
		}
		// For development mode when the lib and classes dir is empty
		if (libs.isEmpty()) {
			modules.add("classpath*:**/devproof-module.xml");
		}
		String[] configs = convertListToArray(modules);
		applicationContext.setConfigLocations(configs);
		applicationContext.refresh();
	}

	private String[] convertListToArray(List<String> modules) {
		String[] configs = new String[modules.size()];
		int i = 0;
		for (String module : modules) {
			configs[i++] = module;

		}
		return configs;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableWebApplicationContext) applicationContext;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
