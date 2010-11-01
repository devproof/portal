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
package org.devproof.portal.core.module.common.registry;

import org.apache.wicket.Page;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.config.Registry;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;
import org.devproof.portal.core.module.modulemgmt.service.ModuleService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Registry("mainNavigationRegistry")
public class MainNavigationRegistryImpl implements MainNavigationRegistry {
    private PageLocator pageLocator;
    private ModuleService moduleService;

    private final List<Class<? extends Page>> pages = new ArrayList<Class<? extends Page>>();

    @Override
    public List<Class<? extends Page>> getRegisteredPages() {
        // immutable
        return Collections.unmodifiableList(pages);
    }

    @Override
    public void registerPage(Class<? extends Page> page) {
        pages.add(page);
    }

    @Override
    public void registerPages(List<Class<? extends Page>> pages) {
        for (Class<? extends Page> page : pages) {
            registerPage(page);
        }
    }

    @Override
    public void clearRegistry() {
        pages.clear();
    }

    @Override
    public void removePage(Class<? extends Page> page) {
        pages.remove(page);
    }

    @Override
    public void buildNavigation() {
        clearRegistry();
        Collection<PageConfiguration> confs = pageLocator.getPageConfigurations();
        List<ModuleLinkEntity> links = moduleService.findAllVisibleMainNavigationLinks();
        for (ModuleLinkEntity link : links) {
            PageConfiguration conf = PortalUtil.getConfigurationByPageName(confs, link.getPageName());
            if (conf != null) {
                registerPage(conf.getPageClass());
            }
        }
    }

    @PostConstruct
    public void afterPropertiesSet() throws Exception {
        buildNavigation();
    }

    @Autowired
    public void setPageLocator(PageLocator pageLocator) {
        this.pageLocator = pageLocator;
    }

    @Autowired
    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

}
