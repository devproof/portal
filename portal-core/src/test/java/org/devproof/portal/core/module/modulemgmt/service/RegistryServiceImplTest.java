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

import org.devproof.portal.core.module.common.registry.GlobalAdminPageRegistry;
import org.devproof.portal.core.module.common.registry.MainNavigationRegistry;
import org.devproof.portal.core.module.common.registry.PageAdminPageRegistry;
import org.devproof.portal.core.module.common.service.RegistryServiceImpl;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * @author Carsten Hufe
 */
public class RegistryServiceImplTest {
    private RegistryServiceImpl impl;
    private MainNavigationRegistry mainNavigationRegistryMock;
    private GlobalAdminPageRegistry globalAdminPageRegistryMock;
    private PageAdminPageRegistry pageAdminPageRegistryMock;

    @Before
    public void setUp() throws Exception {
        mainNavigationRegistryMock = createStrictMock(MainNavigationRegistry.class);
        globalAdminPageRegistryMock = createStrictMock(GlobalAdminPageRegistry.class);
        pageAdminPageRegistryMock = createStrictMock(PageAdminPageRegistry.class);
        impl = new RegistryServiceImpl();
        impl.setMainNavigationRegistry(mainNavigationRegistryMock);
        impl.setGlobalAdminPageRegistry(globalAdminPageRegistryMock);
        impl.setPageAdminPageRegistry(pageAdminPageRegistryMock);
    }

    @Test
    public void testRebuildRegistries1() {
        globalAdminPageRegistryMock.buildNavigation();
        replay(globalAdminPageRegistryMock);
        impl.rebuildRegistries(LinkType.GLOBAL_ADMINISTRATION);
        verify(globalAdminPageRegistryMock);
    }

    @Test
    public void testRebuildRegistries2() {
        mainNavigationRegistryMock.buildNavigation();
        replay(mainNavigationRegistryMock);
        impl.rebuildRegistries(LinkType.TOP_NAVIGATION);
        verify(mainNavigationRegistryMock);
    }

    @Test
    public void testRebuildRegistries3() {
        pageAdminPageRegistryMock.buildNavigation();
        replay(pageAdminPageRegistryMock);
        impl.rebuildRegistries(LinkType.PAGE_ADMINISTRATION);
        verify(pageAdminPageRegistryMock);
    }
}
