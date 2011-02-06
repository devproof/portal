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
package org.devproof.portal.core.module.feed.registry;

import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.DummyFeedProviderImpl;
import org.devproof.portal.core.module.feed.DummyPage;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
public class FeedProviderRegistryImplTest {
    private FeedProviderRegistryImpl impl;

    @Before
    public void setUp() throws Exception {
        impl = new FeedProviderRegistryImpl();
        impl.setPageLocator(new PageLocator() {
            @Override
            public Collection<PageConfiguration> getPageConfigurations() {
                PageConfiguration page = new PageConfiguration();
                page.setPageClass(DummyPage.class);
                page.setMountPath("dummy");
                return Arrays.asList(page);
            }
        });
        impl.registerFeedProvider("dummy", new DummyFeedProviderImpl());
    }

    @Test
    public void testGetAllFeedProvider() {
        Map<String, FeedProvider> allFeedProvider = impl.getAllFeedProvider();
        assertNotNull(allFeedProvider);
        assertEquals(1, allFeedProvider.size());
        assertNotNull(allFeedProvider.get("dummy"));
    }

    @Test
    public void testGetFeedProviderByPath() {
        assertTrue(impl.getFeedProviderByPath("dummy") instanceof DummyFeedProviderImpl);
    }

    @Test
    public void testRegisterFeedProvider() {
        assertEquals(1, impl.getAllFeedProvider().size());
        impl.registerFeedProvider("dummy2", new DummyFeedProviderImpl());
        assertEquals(2, impl.getAllFeedProvider().size());
        assertNotNull(impl.getFeedProviderByPath("dummy2"));
    }

    @Test
    public void testRemoveFeedProvider() {
        impl.removeFeedProvider("dummy");
        assertEquals(0, impl.getAllFeedProvider().size());
    }

    @Test
    public void testGetPathByPageClass() {
        assertEquals("dummy", impl.getPathByPageClass(DummyPage.class));
    }

    @Test
    public void testHasFeedSupport() {
        assertTrue(impl.hasFeedSupport(DummyPage.class));
        assertFalse(impl.hasFeedSupport(TemplatePage.class));
    }
}
