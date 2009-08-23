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
package org.devproof.portal.core.module.role.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.role.dao.RoleDao;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class RoleServiceImplTest extends TestCase {
	private RoleServiceImpl impl;
	private RoleDao mock;

	@Override
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(RoleDao.class);
		impl = new RoleServiceImpl();
		impl.setRoleDao(mock);
	}

	public void testSave() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		mock.save(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(impl.newRoleEntity());
		list.add(impl.newRoleEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewRoleEntity() {
		assertNotNull(impl.newRoleEntity());
	}

	public void testFindAllOrderByDescription() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(impl.newRoleEntity());
		list.add(impl.newRoleEntity());
		EasyMock.expect(mock.findAllOrderByDescription()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAllOrderByDescription());
		EasyMock.verify(mock);
	}
}
