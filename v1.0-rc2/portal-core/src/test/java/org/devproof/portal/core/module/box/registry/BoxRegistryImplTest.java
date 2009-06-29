/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.box.registry;

import junit.framework.TestCase;

import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.module.box.locator.BoxLocator;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class BoxRegistryImplTest extends TestCase {
	private BoxRegistryImpl impl;
	private BoxLocator boxLocatorMock;

	@Override
	public void setUp() throws Exception {
		this.boxLocatorMock = EasyMock.createStrictMock(BoxLocator.class);
		this.impl = new BoxRegistryImpl();
		this.impl.setBoxLocator(this.boxLocatorMock);
	}

	public void testGetRegisteredGlobalAdminPages() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		this.impl.registerBox(box);
		assertEquals(box, this.impl.getRegisteredBoxes().get(0));
	}

	public void testRegisterBox() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		this.impl.registerBox(box);
		assertEquals(box, this.impl.getRegisteredBoxes().get(0));
	}

	public void testRemoveBox() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		this.impl.registerBox(box);
		assertEquals(1, this.impl.getRegisteredBoxes().size());
		this.impl.removeBox(box);
		assertEquals(0, this.impl.getRegisteredBoxes().size());

	}

	public void testGetBoxConfigurationBySimpleClassName() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		this.impl.registerBox(box);
		BoxConfiguration config = this.impl.getBoxConfigurationBySimpleClassName(Panel.class.getSimpleName());
		assertEquals(config.getBoxClass(), Panel.class);
	}

	public void testGetClassBySimpleClassName() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		this.impl.registerBox(box);
		Class<?> clazz = this.impl.getClassBySimpleClassName(Panel.class.getSimpleName());
		assertEquals(clazz, Panel.class);
	}

	public void testGetNameBySimpleClassName() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		box.setName("foobar");
		this.impl.registerBox(box);
		String name = this.impl.getNameBySimpleClassName(Panel.class.getSimpleName());
		assertEquals(name, "foobar");

	}

	public void testIsBoxClassRegistered() {
		BoxConfiguration box = new BoxConfiguration();
		box.setBoxClass(Panel.class);
		this.impl.registerBox(box);
		assertTrue(this.impl.isBoxClassRegistered(Panel.class.getSimpleName()));
		assertFalse(this.impl.isBoxClassRegistered("doesnotexist"));

	}
}
