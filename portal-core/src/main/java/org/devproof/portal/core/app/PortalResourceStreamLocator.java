/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.apache.wicket.util.resource.locator.ResourceStreamLocator;
import org.devproof.portal.core.module.theme.ThemeConstants;

import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resource stream locator, required to managed more then one theme
 * 
 * @author Carsten Hufe
 * 
 */
public class PortalResourceStreamLocator extends ResourceStreamLocator {
	private ServletContext servletContext;
	private String themeUuid;

	public PortalResourceStreamLocator(ServletContext servletContext, String themeUuid) {
		this.servletContext = servletContext;
		setThemeUuid(themeUuid);
	}

	@Override
	public IResourceStream locate(Class<?> clazz, String path) {
		// try to load the resource from the web context
		if (themeUuid != null) {
			try {
				StringBuilder b = new StringBuilder();
				b.append("/WEB-INF/themes/").append(themeUuid).append("/").append(path);
				URL url = servletContext.getResource(b.toString());
				if (url != null) {
					return new UrlResourceStream(url);
				}
			} catch (MalformedURLException e) {
				throw new UnhandledException("Must not occur!", e);
			}
		}
		return super.locate(clazz, path);
	}

	public void setThemeUuid(String themeUuid) {
		if (ThemeConstants.CONF_SELECTED_THEME_DEFAULT.equals(themeUuid)) {
			this.themeUuid = null;
		} else {
			this.themeUuid = themeUuid;
		}
	}
}
