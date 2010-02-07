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
		mock = EasyMock.createStrictMock(BoxDao.class);
		impl = new BoxServiceImpl();
		impl.setBoxDao(mock);
	}

	public void testSave() {
		BoxEntity e = impl.newBoxEntity();
		e.setId(1);
		EasyMock.expect(mock.save(e)).andReturn(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		BoxEntity e = impl.newBoxEntity();
		e.setId(1);
		e.setSort(1);
		EasyMock.expect(mock.getMaxSortNum()).andReturn(1);
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		List<BoxEntity> list = new ArrayList<BoxEntity>();
		list.add(impl.newBoxEntity());
		list.add(impl.newBoxEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		BoxEntity e = impl.newBoxEntity();
		e.setId(1);
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewBoxEntity() {
		assertNotNull(impl.newBoxEntity());
	}

	public void testFindAllOrderedBySort() {
		List<BoxEntity> list = new ArrayList<BoxEntity>();
		list.add(impl.newBoxEntity());
		list.add(impl.newBoxEntity());
		EasyMock.expect(mock.findAllOrderedBySort()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAllOrderedBySort());
		EasyMock.verify(mock);
	}

	public void testFindBoxBySort() {
		BoxEntity e = impl.newBoxEntity();
		e.setId(1);
		EasyMock.expect(mock.findBoxBySort(5)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(e, impl.findBoxBySort(5));
		EasyMock.verify(mock);
	}

	public void testGetMaxSortNum() {
		EasyMock.expect(mock.getMaxSortNum()).andReturn(5);
		EasyMock.replay(mock);
		assertEquals(Integer.valueOf(6), impl.getMaxSortNum());
		EasyMock.verify(mock);
	}

	public void testMoveDown() {
		BoxEntity e1 = impl.newBoxEntity();
		e1.setId(1);
		e1.setSort(1);
		BoxEntity e2 = impl.newBoxEntity();
		e2.setId(2);
		e2.setSort(2);
		EasyMock.expect(mock.getMaxSortNum()).andReturn(2);
		EasyMock.expect(mock.findBoxBySort(2)).andReturn(e2);
		EasyMock.expect(mock.save(e2)).andReturn(e2);
		EasyMock.expect(mock.save(e1)).andReturn(e1);
		EasyMock.replay(mock);
		impl.moveDown(e1);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		EasyMock.verify(mock);
	}

	public void testMoveUp() {
		BoxEntity e1 = impl.newBoxEntity();
		e1.setId(1);
		e1.setSort(1);
		BoxEntity e2 = impl.newBoxEntity();
		e2.setId(2);
		e2.setSort(2);
		EasyMock.expect(mock.findBoxBySort(1)).andReturn(e1);
		EasyMock.expect(mock.save(e2)).andReturn(e2);
		EasyMock.expect(mock.save(e1)).andReturn(e1);
		EasyMock.replay(mock);
		impl.moveUp(e2);
		assertEquals(Integer.valueOf(2), e1.getSort());
		assertEquals(Integer.valueOf(1), e2.getSort());
		EasyMock.verify(mock);
	}
}
