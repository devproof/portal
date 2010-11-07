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

import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink;
import org.devproof.portal.core.module.modulemgmt.service.ModuleService;
import org.devproof.portal.core.module.right.page.RightPage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Carsten Hufe
 */
public class PageAdminPageRegistryImplTest {
    private PageAdminPageRegistryImpl impl;
    private PageLocator pageLocatorMock;
    private ModuleService moduleServiceMock;

    @Before
    public void setUp() throws Exception {
        pageLocatorMock = createStrictMock(PageLocator.class);
        moduleServiceMock = createStrictMock(ModuleService.class);
        impl = new PageAdminPageRegistryImpl();
        impl.setModuleService(moduleServiceMock);
        impl.setPageLocator(pageLocatorMock);
    }

    @Test
    public void testGetRegisteredPages() {
        impl.registerPageAdminPage(RightPage.class);
        assertEquals(RightPage.class, impl.getRegisteredPageAdminPages().get(0));
    }

    @Test
    public void testBuildNavigation() {
        Collection<PageConfiguration> confs = new ArrayList<PageConfiguration>();
        PageConfiguration conf = new PageConfiguration();
        conf.setPageClass(RightPage.class);
        confs.add(conf);
        expect(pageLocatorMock.getPageConfigurations()).andReturn(confs);
        List<ModuleLink> links = new ArrayList<ModuleLink>();
        ModuleLink link = new ModuleLink();
        link.setPageName(RightPage.class.getSimpleName());
        links.add(link);
        expect(moduleServiceMock.findAllVisiblePageAdministrationLinks()).andReturn(links);
        replay(pageLocatorMock, moduleServiceMock);
        impl.buildNavigation();
        verify(pageLocatorMock, moduleServiceMock);
        assertEquals(RightPage.class, impl.getRegisteredPageAdminPages().get(0));
    }

    @Test
    public void testAfterPropertiesSet() throws Exception {
        Collection<PageConfiguration> confs = new ArrayList<PageConfiguration>();
        PageConfiguration conf = new PageConfiguration();
        conf.setPageClass(RightPage.class);
        confs.add(conf);
        expect(pageLocatorMock.getPageConfigurations()).andReturn(confs);
        List<ModuleLink> links = new ArrayList<ModuleLink>();
        ModuleLink link = new ModuleLink();
        link.setPageName(RightPage.class.getSimpleName());
        links.add(link);
        expect(moduleServiceMock.findAllVisiblePageAdministrationLinks()).andReturn(links);
        replay(pageLocatorMock, moduleServiceMock);
        impl.buildNavigation();
        verify(pageLocatorMock, moduleServiceMock);
        assertEquals(RightPage.class, impl.getRegisteredPageAdminPages().get(0));
    }

    @Test
    public void testRegisterPage() {
        impl.registerPageAdminPage(RightPage.class);
        assertEquals(impl.getRegisteredPageAdminPages().get(0), RightPage.class);
    }

    @Test
    public void testRemovePage() {
        impl.registerPageAdminPage(RightPage.class);
        assertEquals(1, impl.getRegisteredPageAdminPages().size());
        impl.removePageAdminPage(RightPage.class);
        assertEquals(0, impl.getRegisteredPageAdminPages().size());
    }
}
