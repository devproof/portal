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
package org.devproof.portal.core.module.modulemgmt.service;

import org.devproof.portal.core.module.modulemgmt.bean.ModuleBean;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink;

import java.util.List;

/**
 * Provides methodes to retrieve module information
 *
 * @author carsten
 */
public interface ModuleService {
    /*
	 * Rebuilds the module links in the database, protected for unit test
	 */
	public void rebuildModuleLinks();
    /**
     * Finds all modules, the list starts with the core modules
     */
    List<ModuleBean> findModules();

    /**
     * Moves the link down
     */
    void moveDown(ModuleLink link);

    /**
     * Moves the link up
     */
    void moveUp(ModuleLink link);

    /**
     * Save the module link
     */
    void save(ModuleLink link);

    /**
     * @return all visible main/top navigation links
     */
    List<ModuleLink> findAllVisibleMainNavigationLinks();

    /**
     * @return all visible global administration links
     */
    List<ModuleLink> findAllVisibleGlobalAdministrationLinks();

    /**
     * @return all visible page administration links
     */
    List<ModuleLink> findAllVisiblePageAdministrationLinks();
}
