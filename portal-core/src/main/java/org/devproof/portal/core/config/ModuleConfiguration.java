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
package org.devproof.portal.core.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Contains the configuration of a module
 * 
 * @author Carsten Hufe
 * 
 */
public class ModuleConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private String moduleVersion;
	private String author;
	private String portalVersion;
	private String url;
	private List<Class<?>> entities = new ArrayList<Class<?>>();
	// mounting etc
	private Collection<PageConfiguration> pages = new ArrayList<PageConfiguration>();
	private Collection<BoxConfiguration> boxes = new ArrayList<BoxConfiguration>();

	/**
	 * @return name of the module
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            name of the module
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return module version
	 */
	public String getModuleVersion() {
		return moduleVersion;
	}

	/**
	 * @param moduleVersion
	 *            module version
	 */
	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	/**
	 * @return author of the module
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            author of the module
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return portal version (written for devproof portal version ...)
	 */
	public String getPortalVersion() {
		return portalVersion;
	}

	/**
	 * @param portalVersion
	 *            portal version (written for devproof portal version ...)
	 */
	public void setPortalVersion(String portalVersion) {
		this.portalVersion = portalVersion;
	}

	/**
	 * @return list with module related entities (JPA and hibernate annotations
	 *         are possible)
	 */
	public List<Class<?>> getEntities() {
		return entities;
	}

	/**
	 * @param entities
	 *            list with module related entities (JPA and hibernate
	 *            annotations are possible)
	 */
	public void setEntities(List<Class<?>> entities) {
		this.entities = entities;
	}

	/**
	 * @return list with page configurations {@link PageConfiguration}
	 */
	public Collection<PageConfiguration> getPages() {
		return pages;
	}

	/**
	 * @param pages
	 *            list with page {@link PageConfiguration}
	 */
	public void setPages(Collection<PageConfiguration> pages) {
		this.pages = pages;
	}

	/**
	 * @return List with boxes (right navigation) {@link BoxConfiguration}
	 */
	public Collection<BoxConfiguration> getBoxes() {
		return boxes;
	}

	/**
	 * @param boxes
	 *            List with boxes (right navigation) {@link BoxConfiguration}
	 */
	public void setBoxes(Collection<BoxConfiguration> boxes) {
		this.boxes = boxes;
	}

	/**
	 * @return url of the authors homepage
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            url of the authors homepage
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name;
	}

}
