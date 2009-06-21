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
package org.devproof.portal.core.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ServletContextAware;

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
		Set<String> libs = this.servletContext.getResourcePaths("/WEB-INF/lib");
		for (String lib : libs) {
			URL url = this.servletContext.getResource(lib);
			JarFile file = new JarFile(url.getFile());
			Enumeration<JarEntry> entries = file.entries();
			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();
				if (jarEntry.getName().endsWith(DEVPROOF_MODULE_XML)) {
					modules.add("classpath:/" + jarEntry.getName());
				}
			}
		}

		// Webstart modules
		if (libs.isEmpty()) {
			Resource modulesTxt = this.applicationContext.getResource("classpath:/devproof-modules.txt");
			if (modulesTxt.exists()) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(modulesTxt.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					modules.add("classpath:/" + line);
				}
				reader.close();
			}
		}
		// For development mode when the lib and classes dir is empty
		if (libs.isEmpty()) {
			modules.add("classpath*:**/devproof-module.xml");
		}
		String[] configs = convertListToArray(modules);
		this.applicationContext.setConfigLocations(configs);
		this.applicationContext.refresh();
	}

	private String[] convertListToArray(final List<String> modules) {
		String[] configs = new String[modules.size()];
		int i = 0;
		for (String module : modules) {
			configs[i++] = module;

		}
		return configs;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = (ConfigurableWebApplicationContext) applicationContext;
	}

	@Override
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}
}
