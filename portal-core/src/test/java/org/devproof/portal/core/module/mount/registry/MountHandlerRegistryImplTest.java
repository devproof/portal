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
package org.devproof.portal.core.module.mount.registry;

import org.devproof.portal.core.module.mount.locator.MountHandlerLocator;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;
import static org.junit.Assert.assertFalse;

/**
 * @author Carsten Hufe
 */
public class MountHandlerRegistryImplTest {
    private MountHandlerRegistryImpl impl;

    @Before
    public void setUp() throws Exception {
        impl = new MountHandlerRegistryImpl();
        impl.setMountHandlerLocator(new MountHandlerLocator() {
            @Override
            public Collection<MountHandler> getMountHandlers() {
                List<MountHandler> handlers = new ArrayList<MountHandler>();
                // TODO fix me
//                handlers.add(new DummyMountHandler());
                return handlers;
            }
        });
        impl.afterPropertiesSet();
    }

    @Test
    public void testRegisterMountHandler() throws Exception {
        // TODO fix me
//        impl.registerMountHandler("dummy2", new DummyMountHandler());
        assertEquals(2, impl.getRegisteredMountHandlers().size());
    }

    @Test
    public void testRemoveMountHandler() throws Exception {
        impl.removeMountHandler("dummy");
        assertEquals(0, impl.getRegisteredMountHandlers().size());
    }

    @Test
    public void testGetMountHandler() throws Exception {
        assertNotNull(impl.getMountHandler("dummy"));
    }

    @Test
    public void testIsMountHandlerAvailable_true() throws Exception {
        assertTrue(impl.isMountHandlerAvailable("dummy"));
    }

    @Test
    public void testIsMountHandlerAvailable_false() throws Exception {
        assertFalse(impl.isMountHandlerAvailable("dummy2"));
    }

    @Test
    public void testGetRegisteredMountHandlers() throws Exception {
        assertEquals(1, impl.getRegisteredMountHandlers().size());
    }

// TODO fixme
//
//    private static class DummyMountHandler implements MountHandler {
//        @Override
//        public IRequestTarget getRequestTarget(String requestedUrl, MountPoint mountPoint) {
//            return null;
//        }
//
//        @Override
//        public String getHandlerKey() {
//            return "dummy";
//        }
//
//        @Override
//        public boolean canHandlePageClass(Class<? extends Page> pageClazz, PageParameters pageParameters) {
//            return false;
//        }
//
//        @Override
//        public String urlFor(Class<? extends Page> pageClazz, PageParameters params) {
//            return null;
//        }
//    }
}
