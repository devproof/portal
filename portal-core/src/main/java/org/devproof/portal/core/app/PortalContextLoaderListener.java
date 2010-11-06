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
package org.devproof.portal.core.app;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Loads all neccesary spring contexts and modules
 *
 * @author Carsten Hufe
 */
public class PortalContextLoaderListener extends ContextLoaderListener {
    private static final String DEVPROOF_MODULE_XML = "devproof-module.xml";

    @Override
    protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext applicationContext) {
        String[] configs = locateConfigLocations();
        applicationContext.setConfigLocations(configs);
    }

    public static String[] locateConfigLocations(String... additionalContexts)  {
        try {
            List<String> modules = new ArrayList<String>();
            List<Properties> propertiesList = loadAllProperties("META-INF/devproof.module", null);
            for(Properties properties : propertiesList) {
                Set<Map.Entry<Object, Object>> entries = properties.entrySet();
                for(Map.Entry<Object, Object> entry : entries) {
                    modules.add((String)entry.getValue());
                }
            }
            for(String additionalContext : additionalContexts) {
                modules.add(additionalContext);
            }
            return convertListToArray(modules);
        }
        catch(Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static List<Properties> loadAllProperties(String resourceName, ClassLoader classLoader) throws IOException {
		Assert.notNull(resourceName, "Resource name must not be null");
		ClassLoader clToUse = classLoader;
		if (clToUse == null) {
			clToUse = ClassUtils.getDefaultClassLoader();
		}
		List<Properties> properties = new ArrayList<Properties>();
		Enumeration urls = clToUse.getResources(resourceName);
		while (urls.hasMoreElements()) {
			URL url = (URL) urls.nextElement();
			InputStream is = null;
			try {
				URLConnection con = url.openConnection();
				con.setUseCaches(false);
				is = con.getInputStream();
                Properties prop = new Properties();
				prop.load(is);
                properties.add(prop);
			}
			finally {
				if (is != null) {
					is.close();
				}
			}
		}
		return properties;
	}

    private static String[] convertListToArray(List<String> modules) {
        String[] configs = new String[modules.size()];
        int i = 0;
        for (String module : modules) {
            configs[i++] = module;

        }
        return configs;
    }    
}
