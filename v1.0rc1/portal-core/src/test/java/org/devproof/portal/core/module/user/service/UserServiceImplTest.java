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
package org.devproof.portal.core.module.user.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.user.dao.UserDao;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class UserServiceImplTest extends TestCase {
	private UserServiceImpl impl;
	private UserDao mock;

	@Override
	public void setUp() throws Exception {
		this.mock = EasyMock.createStrictMock(UserDao.class);
		this.impl = new UserServiceImpl();
		this.impl.setUserDao(this.mock);
	}

	public void testSave() {
		UserEntity e = this.impl.newUserEntity();
		e.setId(1);
		this.mock.save(e);
		EasyMock.replay(this.mock);
		this.impl.save(e);
		EasyMock.verify(this.mock);
	}

	public void testDelete() {
		UserEntity e = this.impl.newUserEntity();
		e.setId(1);
		this.mock.delete(e);
		EasyMock.replay(this.mock);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
	}

	public void testFindAll() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(this.impl.newUserEntity());
		list.add(this.impl.newUserEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		UserEntity e = this.impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewUserEntity() {
		assertNotNull(this.impl.newUserEntity());
	}

	public void testCountUserForRole() {
		RoleEntity role = new RoleEntity();
		EasyMock.expect(this.mock.countUserForRole(role)).andReturn(4l);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.countUserForRole(role), 4l);
		EasyMock.verify(this.mock);
	}

	public void testExistsUsername() {
		EasyMock.expect(this.mock.existsUsername("username")).andReturn(1l);
		EasyMock.replay(this.mock);
		assertTrue(this.impl.existsUsername("username"));
		EasyMock.verify(this.mock);
	}

	public void testFindUserByEmail() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(this.impl.newUserEntity());
		list.add(this.impl.newUserEntity());
		EasyMock.expect(this.mock.findUserByEmail("email@email.org")).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findUserByEmail("email@email.org"), list);
		EasyMock.verify(this.mock);
	}

	public void testFindUserBySessionId() {
		UserEntity e = this.impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findUserBySessionId("12345")).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findUserBySessionId("12345"), e);
		EasyMock.verify(this.mock);
	}

	public void testFindUserByUsername() {
		UserEntity e = this.impl.newUserEntity();
		e.setId(1);
		e.setUsername("username");
		EasyMock.expect(this.mock.findUserByUsername(e.getUsername())).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findUserByUsername(e.getUsername()), e);
		EasyMock.verify(this.mock);
	}

	public void testFindUserWithRight() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(this.impl.newUserEntity());
		list.add(this.impl.newUserEntity());
		EasyMock.expect(this.mock.findUserWithRight("right")).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findUserWithRight("right"), list);
		EasyMock.verify(this.mock);
	}
}
