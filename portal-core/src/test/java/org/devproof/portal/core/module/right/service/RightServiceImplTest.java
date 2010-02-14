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
package org.devproof.portal.core.module.right.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.right.dao.RightDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class RightServiceImplTest extends TestCase {
	private RightServiceImpl impl;
	private RightDao mock;

	@Override
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(RightDao.class);
		impl = new RightServiceImpl();
		impl.setRightDao(mock);
	}

	public void testSave() {
		RightEntity e = impl.newRightEntity();
		e.setRight("right");
		EasyMock.expect(mock.save(e)).andReturn(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		RightEntity e = impl.newRightEntity();
		e.setRight("right");
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		RightEntity e = impl.newRightEntity();
		e.setRight("right");
		EasyMock.expect(mock.findById("right")).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById("right"), e);
		EasyMock.verify(mock);
	}

	public void testNewRightEntity() {
		assertNotNull(impl.newRightEntity());
	}

	public void testNewRightEntityParam() {
		RightEntity r = impl.newRightEntity("hello");
		assertNotNull(r);
		assertNotNull(r.getRight());
	}

	public void testGetDirtyTime() {
		impl.refreshGlobalApplicationRights();
		assertTrue(impl.getDirtyTime() > 0);
	}

	public void testFindAllOrderByDescription() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		EasyMock.expect(mock.findAllOrderByDescription()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAllOrderByDescription());
		EasyMock.verify(mock);
	}

	public void testFindRightsStartingWith() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		EasyMock.expect(mock.findRightsStartingWith("prefix")).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findRightsStartingWith("prefix"));
		EasyMock.verify(mock);
	}

	public void testGetAllRights() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		impl.init();
		assertEquals(impl.getAllRights(), list);
		EasyMock.verify(mock);
	}
}
