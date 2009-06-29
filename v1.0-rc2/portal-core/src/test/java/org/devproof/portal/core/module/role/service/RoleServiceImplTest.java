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
		this.mock = EasyMock.createStrictMock(RoleDao.class);
		this.impl = new RoleServiceImpl();
		this.impl.setRoleDao(this.mock);
	}

	public void testSave() {
		RoleEntity e = this.impl.newRoleEntity();
		e.setId(1);
		this.mock.save(e);
		EasyMock.replay(this.mock);
		this.impl.save(e);
		EasyMock.verify(this.mock);
	}

	public void testDelete() {
		RoleEntity e = this.impl.newRoleEntity();
		e.setId(1);
		this.mock.delete(e);
		EasyMock.replay(this.mock);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
	}

	public void testFindAll() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(this.impl.newRoleEntity());
		list.add(this.impl.newRoleEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		RoleEntity e = this.impl.newRoleEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewRoleEntity() {
		assertNotNull(this.impl.newRoleEntity());
	}

	public void testFindAllOrderByDescription() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(this.impl.newRoleEntity());
		list.add(this.impl.newRoleEntity());
		EasyMock.expect(this.mock.findAllOrderByDescription()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAllOrderByDescription());
		EasyMock.verify(this.mock);
	}
}
