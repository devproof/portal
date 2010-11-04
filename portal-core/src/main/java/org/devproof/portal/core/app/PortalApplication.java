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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.*;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
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

import java.util.Collection;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class PortalApplication extends WebApplication {
    private final Log logger = LogFactory.getLog(PortalApplication.class);
    private Class<? extends Page> startPage;
    private boolean productionMode = false;

    @Override
    protected void init() {
        super.init();
        configureWicket();
        mountPagesAndSetStartPage();
        loadTheme();
        logger.info("Portal is initialized!");
    }

    private void loadTheme() {
        ConfigurationService configurationService = this.getSpringBean("configurationService");
        String themeUuid = configurationService.findAsString(ThemeConstants.CONF_SELECTED_THEME_UUID);
        getResourceSettings().setResourceStreamLocator(new PortalResourceStreamLocator(getServletContext(), themeUuid));
    }

    private void mountPagesAndSetStartPage() {
        startPage = NoStartPage.class;
        PageLocator pageLocator = this.getSpringBean("pageLocator");
        MainNavigationRegistry mainNavigationRegistry = this.getSpringBean("mainNavigationRegistry");
        Collection<PageConfiguration> pages = pageLocator.getPageConfigurations();
        for (PageConfiguration page : pages) {
            if (page.getMountPath() != null) {
                if (page.isIndexMountedPath()) {
                    mount(new IndexedParamUrlCodingStrategy(page.getMountPath(), page.getPageClass()));
                } else {
                    mountBookmarkablePage(page.getMountPath(), page.getPageClass());
                }
            }
        }

        List<Class<? extends Page>> registeredPages = mainNavigationRegistry.getRegisteredPages();
        if (!registeredPages.isEmpty()) {
            // First visible page in the main navigation is the startpage!
            startPage = registeredPages.get(0);
        }
    }

    private void configureWicket() {
        productionMode = DEPLOYMENT.equals(getConfigurationType());
        getResourceSettings().setThrowExceptionOnMissingResource(false);
        addComponentInstantiationListener(new SpringComponentInjector(this, getSpringContext(), true));
        getMarkupSettings().setStripWicketTags(true);
        getMarkupSettings().setCompressWhitespace(true);
        getMarkupSettings().setStripComments(true);
        getSecuritySettings().setAuthorizationStrategy(new PortalAuthorizationStrategy(getSpringContext()));
        getApplicationSettings().setAccessDeniedPage(AccessDeniedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(PageExpiredPage.class);
        getApplicationSettings().setInternalErrorPage(InternalErrorPage.class);
    }

    public boolean isProductionMode() {
        return productionMode;
    }

    public void setThemeUuid(String themeUuid) {
        PortalResourceStreamLocator locator = (PortalResourceStreamLocator) getResourceSettings().getResourceStreamLocator();
        locator.setThemeUuid(themeUuid);
        getMarkupSettings().getMarkupCache().clear();
        logger.debug("Theme " + themeUuid + " selected.");
    }

    /**
     * Rollback on runtime exception Inform the admin about the runtime
     * exception
     */
    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor() {
        return new PortalRequestCycleProcessor(getSpringContext());
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return startPage;
    }

    public ApplicationContext getSpringContext() {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    }

    @SuppressWarnings("unchecked")
    public <T> T getSpringBean(String id) {
        return (T) getSpringContext().getBean(id);
    }

    @Override
    public Session newSession(Request request, Response response) {
        logger.debug("New session created.");
        return new PortalSession(request);
    }

//    @Override
//    public RequestCycle newRequestCycle(Request request, Response response) {
//        return new PortalWebRequestCycle(this, (WebRequest) request, response, getSpringContext());
//    }
}
