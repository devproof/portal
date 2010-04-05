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
package org.devproof.portal.core.config;

import org.apache.wicket.Page;

import java.io.Serializable;

/**
 * Contains configuration for one page
 * 
 * @author Carsten Hufe
 * 
 */
public class PageConfiguration implements Serializable {
	private static final long serialVersionUID = 1L;
	// if null there is no mount path
	private String mountPath;
	private Class<? extends Page> pageClass;
	private boolean registerMainNavigationLink = false;
	private boolean registerGlobalAdminLink = false;
	private boolean indexMountedPath = false;
	private boolean defaultStartPage = false;
	private boolean registerPageAdminLink = false;
	private ModuleConfiguration module;

	/**
	 * @return the mount path of the page
	 */
	public String getMountPath() {
		return mountPath;
	}

	/**
	 * @param mountPath
	 *            the mount path of the page
	 */
	public void setMountPath(String mountPath) {
		this.mountPath = mountPath;
	}

	/**
	 * @return page class (must extend the wicket page class)
	 */
	public Class<? extends Page> getPageClass() {
		return pageClass;
	}

	/**
	 * @param pageClass
	 *            page class (must extend the wicket page class)
	 */
	public void setPageClass(Class<? extends Page> pageClass) {
		this.pageClass = pageClass;
	}

	/**
	 * @return true if the mount path is indexed: indexed means e.g.
	 *         /hello/arg0/arg1
	 */
	public boolean isIndexMountedPath() {
		return indexMountedPath;
	}

	/**
	 * @param indexMountedPath
	 *            true if the mount path is indexed: indexed means e.g.
	 *            /hello/arg0/arg1
	 */
	public void setIndexMountedPath(boolean indexMountedPath) {
		this.indexMountedPath = indexMountedPath;
	}

	/**
	 * @return true if this page should be the default start page of the portal
	 */
	public boolean isDefaultStartPage() {
		return defaultStartPage;
	}

	/**
	 * @param defaultStartPage
	 *            true if this page should be the default start page of the
	 *            portal
	 */
	public void setDefaultStartPage(boolean defaultStartPage) {
		this.defaultStartPage = defaultStartPage;
	}

	/**
	 * @return true if the page is linked in the global admin box:
	 *         "Global Administration" on the right side. The page properties
	 *         file must contain a value adminLinkLabel for the title
	 */
	public boolean isRegisterGlobalAdminLink() {
		return registerGlobalAdminLink;
	}

	/**
	 * @param registerGlobalAdminLink
	 *            true if the page is linked in the global admin box:
	 *            "Global Administration" on the right side. The page properties
	 *            file must contain a value adminLinkLabel for the title
	 */
	public void setRegisterGlobalAdminLink(boolean registerGlobalAdminLink) {
		this.registerGlobalAdminLink = registerGlobalAdminLink;
	}

	/**
	 * @return true if the page is linked in the site administration box
	 *         "Site administration". E.g. The upload center link is a site
	 *         admin link. The page properties file must contain a value
	 *         adminLinkLabel for the title
	 */
	public boolean isRegisterPageAdminLink() {
		return registerPageAdminLink;
	}

	/**
	 * @param registerPageAdminLink
	 *            true if the page is linked in the site administration box
	 *            "Site administration". E.g. The upload center link is a site
	 *            admin link. The page properties file must contain a value
	 *            adminLinkLabel for the title
	 */
	public void setRegisterPageAdminLink(boolean registerPageAdminLink) {
		this.registerPageAdminLink = registerPageAdminLink;
	}

	/**
	 * @return true if the page registers a top navigation link: e.g. Blog,
	 *         Bookmarks, etc. The page properties file must contain a value
	 *         mainNavigationLinkLabel for the title
	 */
	public boolean isRegisterMainNavigationLink() {
		return registerMainNavigationLink;
	}

	/**
	 * @param registerMainNavigationLink
	 *            true if the page registers a top navigation link: e.g. Blog,
	 *            Bookmarks, etc. The page properties file must contain a value
	 *            mainNavigationLinkLabel for the title
	 */
	public void setRegisterMainNavigationLink(boolean registerMainNavigationLink) {
		this.registerMainNavigationLink = registerMainNavigationLink;
	}

	/**
	 * @return the parent module, must not be set in the spring configuration,
	 *         is only set from the PageLocator
	 */
	public ModuleConfiguration getModule() {
		return module;
	}

	/**
	 * @param module
	 *            the parent module, must not be set in the spring
	 *            configuration, is only set from the PageLocator
	 */
	public void setModule(ModuleConfiguration module) {
		this.module = module;
	}

	@Override
	public String toString() {
		return "PageConfiguration [defaultStartPage=" + defaultStartPage + ", indexMountedPath=" + indexMountedPath
				+ ", module=" + module + ", mountPath=" + mountPath + ", pageClass=" + pageClass
				+ ", registerGlobalAdminLink=" + registerGlobalAdminLink + ", registerMainNavigationLink="
				+ registerMainNavigationLink + ", registerPageAdminLink=" + registerPageAdminLink + "]";
	}
}
