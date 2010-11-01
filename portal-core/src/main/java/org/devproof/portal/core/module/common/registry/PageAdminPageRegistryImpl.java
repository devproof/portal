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
@Registry("pageAdminPageRegistry")
public class PageAdminPageRegistryImpl implements PageAdminPageRegistry {
    private PageLocator pageLocator;
    private ModuleService moduleService;
    private final List<Class<? extends Page>> adminPages = new ArrayList<Class<? extends Page>>();

    @Override
    public List<Class<? extends Page>> getRegisteredPageAdminPages() {
        return Collections.unmodifiableList(adminPages);
    }

    @Override
    public void registerPageAdminPage(Class<? extends Page> adminPage) {
        adminPages.add(adminPage);
    }

    @Override
    public void removePageAdminPage(Class<? extends Page> adminPage) {
        adminPages.remove(adminPage);
    }

    @PostConstruct
    public void afterPropertiesSet() {
        buildNavigation();
    }

    @Override
    public void buildNavigation() {
        adminPages.clear();
        Collection<PageConfiguration> confs = pageLocator.getPageConfigurations();
        List<ModuleLinkEntity> links = moduleService.findAllVisiblePageAdministrationLinks();
        for (ModuleLinkEntity link : links) {
            PageConfiguration conf = PortalUtil.getConfigurationByPageName(confs, link.getPageName());
            if (conf != null) {
                registerPageAdminPage(conf.getPageClass());
            }
        }
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
