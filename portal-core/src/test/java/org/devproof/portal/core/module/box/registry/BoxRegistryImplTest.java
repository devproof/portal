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
package org.devproof.portal.core.module.box.registry;

import junit.framework.TestCase;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.module.box.locator.BoxLocator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createStrictMock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Carsten Hufe
 */
public class BoxRegistryImplTest {
    private BoxRegistryImpl impl;

    @Before
    public void setUp() throws Exception {
        BoxLocator boxLocatorMock = createStrictMock(BoxLocator.class);
        impl = new BoxRegistryImpl();
        impl.setBoxLocator(boxLocatorMock);
    }

    @Test
    public void testGetRegisteredGlobalAdminPages() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        impl.registerBox(box);
        assertEquals(box, impl.getRegisteredBoxes().get(0));
    }

    @Test
    public void testRegisterBox() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        impl.registerBox(box);
        assertEquals(box, impl.getRegisteredBoxes().get(0));
    }

    @Test
    public void testRemoveBox() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        impl.registerBox(box);
        assertEquals(1, impl.getRegisteredBoxes().size());
        impl.removeBox(box);
        assertEquals(0, impl.getRegisteredBoxes().size());

    }

    @Test
    public void testGetBoxConfigurationBySimpleClassName() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        impl.registerBox(box);
        BoxConfiguration config = impl.getBoxConfigurationBySimpleClassName(Panel.class.getSimpleName());
        assertEquals(config.getBoxClass(), Panel.class);
    }

    @Test
    public void testGetClassBySimpleClassName() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        impl.registerBox(box);
        Class<?> clazz = impl.getClassBySimpleClassName(Panel.class.getSimpleName());
        assertEquals(clazz, Panel.class);
    }

    @Test
    public void testGetNameBySimpleClassName() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        box.setName("foobar");
        impl.registerBox(box);
        String name = impl.getNameBySimpleClassName(Panel.class.getSimpleName());
        assertEquals(name, "foobar");
    }

    @Test
    public void testIsBoxClassRegistered() {
        BoxConfiguration box = new BoxConfiguration();
        box.setBoxClass(Panel.class);
        impl.registerBox(box);
        assertTrue(impl.isBoxClassRegistered(Panel.class.getSimpleName()));
        assertFalse(impl.isBoxClassRegistered("doesnotexist"));
    }
}
