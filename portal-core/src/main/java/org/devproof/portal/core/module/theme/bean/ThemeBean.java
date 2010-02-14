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
package org.devproof.portal.core.module.theme.bean;

import java.io.Serializable;

/**
 * Theme bean represents one theme/design
 * 
 * @author Carsten Hufe
 */
public class ThemeBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private String theme;
	private String author;
	private String url;
	private String portalThemeVersion;
	private String portalVersion;
	private String uuid;

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPortalThemeVersion() {
		return portalThemeVersion;
	}

	public void setPortalThemeVersion(String portalThemeVersion) {
		this.portalThemeVersion = portalThemeVersion;
	}

	public String getPortalVersion() {
		return portalVersion;
	}

	public void setPortalVersion(String portalVersion) {
		this.portalVersion = portalVersion;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
