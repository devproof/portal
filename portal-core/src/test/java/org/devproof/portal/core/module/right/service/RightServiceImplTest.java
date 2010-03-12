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

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.right.dao.RightDao;
import org.devproof.portal.core.module.right.entity.RightEntity;

/**
 * @author Carsten Hufe
 */
public class RightServiceImplTest extends TestCase {
	private RightServiceImpl impl;
	private RightDao mock;

	@Override
	public void setUp() throws Exception {
		mock = createStrictMock(RightDao.class);
		impl = new RightServiceImpl();
		impl.setRightDao(mock);
	}

	public void testSave() {
		RightEntity e = impl.newRightEntity();
		e.setRight("right");
		expect(mock.save(e)).andReturn(e);
		replay(mock);
		impl.save(e);
		verify(mock);
	}

	public void testDelete() {
		RightEntity e = impl.newRightEntity();
		e.setRight("right");
		mock.delete(e);
		replay(mock);
		impl.delete(e);
		verify(mock);
	}

	public void testFindAll() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		expect(mock.findAll()).andReturn(list);
		replay(mock);
		assertEquals(list, impl.findAll());
		verify(mock);
	}

	public void testFindById() {
		RightEntity e = impl.newRightEntity();
		e.setRight("right");
		expect(mock.findById("right")).andReturn(e);
		replay(mock);
		assertEquals(impl.findById("right"), e);
		verify(mock);
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
		expect(mock.findAllOrderByDescription()).andReturn(list);
		replay(mock);
		assertEquals(list, impl.findAllOrderByDescription());
		verify(mock);
	}

	public void testFindRightsStartingWith() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		expect(mock.findRightsStartingWith("prefix")).andReturn(list);
		replay(mock);
		assertEquals(list, impl.findRightsStartingWith("prefix"));
		verify(mock);
	}

	public void testGetAllRights() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(impl.newRightEntity());
		list.add(impl.newRightEntity());
		expect(mock.findAll()).andReturn(list);
		replay(mock);
		impl.init();
		assertEquals(impl.getAllRights(), list);
		verify(mock);
	}
}
