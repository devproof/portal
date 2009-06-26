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
package org.devproof.portal.core.module.box.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.box.dao.BoxDao;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class BoxServiceImplTest extends TestCase {
	private BoxServiceImpl impl;
	private BoxDao mock;

	@Override
	public void setUp() throws Exception {
		this.mock = EasyMock.createStrictMock(BoxDao.class);
		this.impl = new BoxServiceImpl();
		this.impl.setBoxDao(this.mock);
	}

	public void testSave() {
		BoxEntity e = this.impl.newBoxEntity();
		e.setId(1);
		this.mock.save(e);
		EasyMock.replay(this.mock);
		this.impl.save(e);
		EasyMock.verify(this.mock);
	}

	public void testDelete() {
		BoxEntity e = this.impl.newBoxEntity();
		e.setId(1);
		e.setSort(1);
		EasyMock.expect(this.mock.getMaxSortNum()).andReturn(1);
		this.mock.delete(e);
		EasyMock.replay(this.mock);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
	}

	public void testFindAll() {
		List<BoxEntity> list = new ArrayList<BoxEntity>();
		list.add(this.impl.newBoxEntity());
		list.add(this.impl.newBoxEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		BoxEntity e = this.impl.newBoxEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewBoxEntity() {
		assertNotNull(this.impl.newBoxEntity());
	}

	public void testFindAllOrderedBySort() {
		List<BoxEntity> list = new ArrayList<BoxEntity>();
		list.add(this.impl.newBoxEntity());
		list.add(this.impl.newBoxEntity());
		EasyMock.expect(this.mock.findAllOrderedBySort()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAllOrderedBySort());
		EasyMock.verify(this.mock);
	}

	public void testFindBoxBySort() {
		BoxEntity e = this.impl.newBoxEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findBoxBySort(5)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(e, this.impl.findBoxBySort(5));
		EasyMock.verify(this.mock);
	}

	public void testGetMaxSortNum() {
		EasyMock.expect(this.mock.getMaxSortNum()).andReturn(5);
		EasyMock.replay(this.mock);
		assertEquals(Integer.valueOf(6), this.impl.getMaxSortNum());
		EasyMock.verify(this.mock);
	}

	public void testMoveDown() {
		BoxEntity e1 = this.impl.newBoxEntity();
		e1.setId(1);
		e1.setSort(1);
		BoxEntity e2 = this.impl.newBoxEntity();
		e2.setId(2);
		e2.setSort(2);
		EasyMock.expect(this.mock.getMaxSortNum()).andReturn(2);
		EasyMock.expect(this.mock.findBoxBySort(2)).andReturn(e2);
		this.mock.save(e2);
		this.mock.save(e1);
		EasyMock.replay(this.mock);
		this.impl.moveDown(e1);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		EasyMock.verify(this.mock);
	}

	public void testMoveUp() {
		BoxEntity e1 = this.impl.newBoxEntity();
		e1.setId(1);
		e1.setSort(1);
		BoxEntity e2 = this.impl.newBoxEntity();
		e2.setId(2);
		e2.setSort(2);
		EasyMock.expect(this.mock.findBoxBySort(1)).andReturn(e1);
		this.mock.save(e2);
		this.mock.save(e1);
		EasyMock.replay(this.mock);
		this.impl.moveUp(e2);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		EasyMock.verify(this.mock);
	}
}
