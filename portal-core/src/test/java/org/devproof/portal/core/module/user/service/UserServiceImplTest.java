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
		mock = EasyMock.createStrictMock(UserDao.class);
		impl = new UserServiceImpl();
		impl.setUserDao(mock);
	}

	public void testSave() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		mock.save(e);
		EasyMock.replay(mock);
		impl.save(e);
		EasyMock.verify(mock);
	}

	public void testDelete() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		mock.delete(e);
		EasyMock.replay(mock);
		impl.delete(e);
		EasyMock.verify(mock);
	}

	public void testFindAll() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewUserEntity() {
		assertNotNull(impl.newUserEntity());
	}

	public void testCountUserForRole() {
		RoleEntity role = new RoleEntity();
		EasyMock.expect(mock.countUserForRole(role)).andReturn(4l);
		EasyMock.replay(mock);
		assertEquals(impl.countUserForRole(role), 4l);
		EasyMock.verify(mock);
	}

	public void testExistsUsername() {
		EasyMock.expect(mock.existsUsername("username")).andReturn(1l);
		EasyMock.replay(mock);
		assertTrue(impl.existsUsername("username"));
		EasyMock.verify(mock);
	}

	public void testFindUserByEmail() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		EasyMock.expect(mock.findUserByEmail("email@email.org")).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(impl.findUserByEmail("email@email.org"), list);
		EasyMock.verify(mock);
	}

	public void testFindUserBySessionId() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(mock.findUserBySessionId("12345")).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findUserBySessionId("12345"), e);
		EasyMock.verify(mock);
	}

	public void testFindUserByUsername() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		e.setUsername("username");
		EasyMock.expect(mock.findUserByUsername(e.getUsername())).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findUserByUsername(e.getUsername()), e);
		EasyMock.verify(mock);
	}

	public void testFindUserWithRight() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		EasyMock.expect(mock.findUserWithRight("right")).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(impl.findUserWithRight("right"), list);
		EasyMock.verify(mock);
	}
}
