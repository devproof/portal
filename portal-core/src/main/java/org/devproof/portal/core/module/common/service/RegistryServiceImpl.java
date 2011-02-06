/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.service;

import org.devproof.portal.core.module.common.registry.GlobalAdminPageRegistry;
import org.devproof.portal.core.module.common.registry.MainNavigationRegistry;
import org.devproof.portal.core.module.common.registry.PageAdminPageRegistry;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink.LinkType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Carsten Hufe
 */
@Service("registryService")
public class RegistryServiceImpl implements RegistryService {
    private MainNavigationRegistry mainNavigationRegistry;
    private GlobalAdminPageRegistry globalAdminPageRegistry;
    private PageAdminPageRegistry pageAdminPageRegistry;

    @Override
    public void rebuildRegistries(LinkType type) {
        if (type == LinkType.TOP_NAVIGATION) {
            mainNavigationRegistry.buildNavigation();
        } else if (type == LinkType.PAGE_ADMINISTRATION) {
            pageAdminPageRegistry.buildNavigation();
        } else if (type == LinkType.GLOBAL_ADMINISTRATION) {
            globalAdminPageRegistry.buildNavigation();
        } else {
            throw new IllegalArgumentException("LinkType " + type + " is currently not supported!");
        }
    }

    @Autowired
    public void setMainNavigationRegistry(MainNavigationRegistry mainNavigationRegistry) {
        this.mainNavigationRegistry = mainNavigationRegistry;
    }

    @Autowired
    public void setGlobalAdminPageRegistry(GlobalAdminPageRegistry globalAdminPageRegistry) {
        this.globalAdminPageRegistry = globalAdminPageRegistry;
    }

    @Autowired
    public void setPageAdminPageRegistry(PageAdminPageRegistry pageAdminPageRegistry) {
        this.pageAdminPageRegistry = pageAdminPageRegistry;
    }
}
