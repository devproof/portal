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
package org.devproof.portal.module.otherpage.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.module.otherpage.dao.OtherPageDao;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.easymock.EasyMock;

/**
 * Checks the delegating functionality of the OtherPageServiceImpl
 * 
 * @author Carsten Hufe
 */
public class OtherPageServiceImplTest extends TestCase {
	private OtherPageServiceImpl impl;
	private OtherPageDao mock;

	@Override
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(OtherPageDao.class);
		impl = new OtherPageServiceImpl();
		impl.setOtherPageDao(mock);
	}

	public void testSave() {
		OtherPageEntity e = createOtherPageEntity();
		EasyMock.expect(mock.save(e)).andReturn(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		OtherPageEntity e = createOtherPageEntity();
		e.setId(1);
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		List<OtherPageEntity> list = new ArrayList<OtherPageEntity>();
		list.add(createOtherPageEntity());
		list.add(createOtherPageEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		OtherPageEntity e = createOtherPageEntity();
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewOtherPageEntity() {
		assertNotNull(impl.newOtherPageEntity());
	}

	public void testExistsContentId() {
		EasyMock.expect(mock.existsContentId("contentId")).andReturn(1l);
		EasyMock.replay(mock);
		assertTrue(impl.existsContentId("contentId"));
		EasyMock.verify(mock);
	}

	private OtherPageEntity createOtherPageEntity() {
		OtherPageEntity e = new OtherPageEntity();
		e.setId(1);
		return e;
	}
}
