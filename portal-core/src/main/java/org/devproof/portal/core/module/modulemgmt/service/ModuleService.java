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
package org.devproof.portal.core.module.modulemgmt.service;

import org.devproof.portal.core.module.modulemgmt.bean.ModuleBean;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;

import java.util.List;

/**
 * Provides methodes to retrieve module information
 *
 * @author carsten
 */
public interface ModuleService {
    /**
     * Finds all modules, the list starts with the core modules
     */
    List<ModuleBean> findModules();

    /**
     * Moves the link down
     */
    void moveDown(ModuleLinkEntity link);

    /**
     * Moves the link up
     */
    void moveUp(ModuleLinkEntity link);

    /**
     * Save the module link
     */
    void save(ModuleLinkEntity link);

    /**
     * @return all visible main/top navigation links
     */
    List<ModuleLinkEntity> findAllVisibleMainNavigationLinks();

    /**
     * @return all visible global administration links
     */
    List<ModuleLinkEntity> findAllVisibleGlobalAdministrationLinks();

    /**
     * @return all visible page administration links
     */
    List<ModuleLinkEntity> findAllVisiblePageAdministrationLinks();
}
