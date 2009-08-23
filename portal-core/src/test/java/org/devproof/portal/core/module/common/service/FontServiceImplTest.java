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
package org.devproof.portal.core.module.common.service;

import junit.framework.TestCase;

/**
 * @author Carsten Hufe
 */
public class FontServiceImplTest extends TestCase {
	private FontServiceImpl impl;

	@Override
	public void setUp() throws Exception {
		impl = new FontServiceImpl();
	}

	public void testFindAllSystemFonts() {
		assertNotNull(impl.findAllSystemFonts());
		assertTrue(impl.findAllSystemFonts().size() > 0);
	}
}
