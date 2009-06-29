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
		this.mock = EasyMock.createStrictMock(RightDao.class);
		this.impl = new RightServiceImpl();
		this.impl.setRightDao(this.mock);
	}

	public void testSave() {
		RightEntity e = this.impl.newRightEntity();
		e.setRight("right");
		this.mock.save(e);
		EasyMock.replay(this.mock);
		this.impl.save(e);
		EasyMock.verify(this.mock);
	}

	public void testDelete() {
		RightEntity e = this.impl.newRightEntity();
		e.setRight("right");
		this.mock.delete(e);
		EasyMock.replay(this.mock);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
	}

	public void testFindAll() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(this.impl.newRightEntity());
		list.add(this.impl.newRightEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		RightEntity e = this.impl.newRightEntity();
		e.setRight("right");
		EasyMock.expect(this.mock.findById("right")).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById("right"), e);
		EasyMock.verify(this.mock);
	}

	public void testNewRightEntity() {
		assertNotNull(this.impl.newRightEntity());
	}

	public void testNewRightEntityParam() {
		RightEntity r = this.impl.newRightEntity("hello");
		assertNotNull(r);
		assertNotNull(r.getRight());
	}

	public void testGetDirtyTime() {
		this.impl.refreshGlobalApplicationRights();
		assertTrue(this.impl.getDirtyTime() > 0);
	}

	public void testFindAllOrderByDescription() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(this.impl.newRightEntity());
		list.add(this.impl.newRightEntity());
		EasyMock.expect(this.mock.findAllOrderByDescription()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAllOrderByDescription());
		EasyMock.verify(this.mock);
	}

	public void testFindRightsStartingWith() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(this.impl.newRightEntity());
		list.add(this.impl.newRightEntity());
		EasyMock.expect(this.mock.findRightsStartingWith("prefix")).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findRightsStartingWith("prefix"));
		EasyMock.verify(this.mock);
	}

	public void testGetAllRights() {
		List<RightEntity> list = new ArrayList<RightEntity>();
		list.add(this.impl.newRightEntity());
		list.add(this.impl.newRightEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		this.impl.init();
		assertEquals(this.impl.getAllRights(), list);
		EasyMock.verify(this.mock);
	}
}
