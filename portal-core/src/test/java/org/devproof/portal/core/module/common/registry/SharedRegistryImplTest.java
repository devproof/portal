/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
package org.devproof.portal.core.module.common.registry;

import junit.framework.TestCase;

/**
 * @author Carsten Hufe
 */
public class SharedRegistryImplTest extends TestCase {
	private SharedRegistryImpl impl;

	@Override
	public void setUp() throws Exception {
		impl = new SharedRegistryImpl();
	}

	public void testGetResource() {
		impl.registerResource("key", "value");
		assertEquals("value", impl.getResource("key"));
	}

	public void testGetRegisteredResources() {
		impl.registerResource("key", "value");
		assertEquals(impl.getRegisteredResources().get("key"), "value");
	}

	public void testIsResourceAvailable() {
		impl.registerResource("key", "value");
		assertTrue(impl.isResourceAvailable("key"));
		assertFalse(impl.isResourceAvailable("key2"));
	}

	public void testRegisterResource() {
		assertEquals(0, impl.getRegisteredResources().size());
		impl.registerResource("key", "value");
		assertEquals(1, impl.getRegisteredResources().size());
	}

	public void testRemoveResource() {
		impl.registerResource("key", "value");
		assertEquals(1, impl.getRegisteredResources().size());
		impl.removeResource("key");
		assertEquals(0, impl.getRegisteredResources().size());
	}
}
