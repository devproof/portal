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
package org.devproof.portal.core.module.common.registry;

import junit.framework.TestCase;

/**
 * @author Carsten Hufe
 */
public class SharedRegistryImplTest extends TestCase {
	private SharedRegistryImpl impl;

	@Override
	public void setUp() throws Exception {
		this.impl = new SharedRegistryImpl();
	}

	public void testGetResource() {
		this.impl.registerResource("key", "value");
		assertEquals("value", this.impl.getResource("key"));
	}

	public void testGetRegisteredResources() {
		this.impl.registerResource("key", "value");
		assertEquals(this.impl.getRegisteredResources().get("key"), "value");
	}

	public void testIsResourceAvailable() {
		this.impl.registerResource("key", "value");
		assertTrue(this.impl.isResourceAvailable("key"));
		assertFalse(this.impl.isResourceAvailable("key2"));
	}

	public void testRegisterResource() {
		assertEquals(0, this.impl.getRegisteredResources().size());
		this.impl.registerResource("key", "value");
		assertEquals(1, this.impl.getRegisteredResources().size());
	}

	public void testRemoveResource() {
		this.impl.registerResource("key", "value");
		assertEquals(1, this.impl.getRegisteredResources().size());
		this.impl.removeResource("key");
		assertEquals(0, this.impl.getRegisteredResources().size());
	}
}
