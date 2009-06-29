/*
 * Copyright 2009 Carsten Hufe devproof.org
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

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.request.target.coding.IndexedParamUrlCodingStrategy;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.common.page.AccessDeniedPage;
import org.devproof.portal.core.module.common.page.InternalErrorPage;
import org.devproof.portal.core.module.common.page.NoStartPage;
import org.devproof.portal.core.module.common.page.PageExpiredPage;
import org.devproof.portal.core.module.common.registry.MainNavigationRegistry;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.theme.ThemeConstants;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Carsten Hufe
 */
public class PortalApplication extends WebApplication {
	private static final Log LOG = LogFactory.getLog(PortalApplication.class);
	private Class<? extends Page> startPage;
	private boolean productionMode = false;

	@Override
	protected void init() {
		super.init();
		configureWicket();
		mountPagesAndSetStartPage();
		loadTheme();
		LOG.info("Portal is initialized!");
	}

	private void loadTheme() {
		final ConfigurationService configurationService = this.getSpringBean("configurationService");
		final String themeUuid = configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
		getResourceSettings().setResourceStreamLocator(new PortalResourceStreamLocator(getServletContext(), themeUuid));
	}

	private void mountPagesAndSetStartPage() {
		this.startPage = NoStartPage.class;
		final PageLocator pageLocator = this.getSpringBean("pageLocator");
		final MainNavigationRegistry mainNavigationRegistry = this.getSpringBean("mainNavigationRegistry");
		final Collection<PageConfiguration> pages = pageLocator.getPageConfigurations();
		for (final PageConfiguration page : pages) {
			if (page.getMountPath() != null) {
				if (page.isIndexMountedPath()) {
					this.mount(new IndexedParamUrlCodingStrategy(page.getMountPath(), page.getPageClass()));
				} else {
					this.mountBookmarkablePage(page.getMountPath(), page.getPageClass());
				}
			}
		}

		List<Class<? extends Page>> registeredPages = mainNavigationRegistry.getRegisteredPages();
		if (!registeredPages.isEmpty()) {
			// First visible page in the main navigation is the startpage!
			this.startPage = registeredPages.get(0);
		}
	}

	private void configureWicket() {
		this.productionMode = !"WicketMockServlet".equals(getApplicationKey());
		getResourceSettings().setThrowExceptionOnMissingResource(false);
		addComponentInstantiationListener(new SpringComponentInjector(this, getSpringContext()));
		getMarkupSettings().setStripWicketTags(true);
		getMarkupSettings().setCompressWhitespace(true);
		getMarkupSettings().setStripComments(true);
		getSecuritySettings().setAuthorizationStrategy(new PortalAuthorizationStrategy(getSpringContext()));
		getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
		getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);
		getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
	}

	public boolean isProductionMode() {
		return this.productionMode;
	}

	public void setThemeUuid(final String themeUuid) {
		final PortalResourceStreamLocator locator = (PortalResourceStreamLocator) getResourceSettings().getResourceStreamLocator();
		locator.setThemeUuid(themeUuid);
		getMarkupSettings().getMarkupCache().clear();
		LOG.debug("Theme " + themeUuid + " selected.");
	}

	/**
	 * Rollback on runtime exception Inform the admin about the runtime
	 * exception
	 */
	@Override
	protected IRequestCycleProcessor newRequestCycleProcessor() {
		return new PortalRequestCycleProcessor(getSpringContext(), isProductionMode());
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public Class getHomePage() {
		return this.startPage;
	}

	public ApplicationContext getSpringContext() {
		return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
	}

	public <T> T getSpringBean(final String id) {
		@SuppressWarnings("unchecked")
		final T back = (T) getSpringContext().getBean(id);
		return back;
	}

	@Override
	public Session newSession(final Request request, final Response response) {
		LOG.debug("New session created.");
		return new PortalSession(request);
	}

	@Override
	public RequestCycle newRequestCycle(final Request request, final Response response) {
		return new PortalWebRequestCycle(this, (WebRequest) request, (WebResponse) response, getSpringContext());
	}
}
