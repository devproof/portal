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
package org.devproof.portal.core.module.common.registry;

import org.apache.wicket.Page;

import java.util.List;

/**
 * Registry for page administration links
 *
 * @author Carsten Hufe
 */
public interface PageAdminPageRegistry {
    /**
     * Registers a page admin link. The language property file of the page must
     * contain a property named "adminLinkLabel" for menu name
     */
    void registerPageAdminPage(Class<? extends Page> adminPage);

    /**
     * Removes a admin link
     */
    void removePageAdminPage(Class<? extends Page> adminPage);

    /**
     * Returns all registered admin pages
     */
    List<Class<? extends Page>> getRegisteredPageAdminPages();

    /**
     * Builds or rebuilds the page administration box from the database
     */
    void buildNavigation();
}
