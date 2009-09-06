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

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.role.RoleConstants;
import org.devproof.portal.core.module.role.dao.RoleDao;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class RoleServiceImplTest extends TestCase {
	private RoleServiceImpl impl;
	private RoleDao roleDaoMock;
	private ConfigurationService configurationServiceMock;

	@Override
	public void setUp() throws Exception {
		roleDaoMock = EasyMock.createStrictMock(RoleDao.class);
		configurationServiceMock = EasyMock.createStrictMock(ConfigurationService.class);
		impl = new RoleServiceImpl();
		impl.setRoleDao(roleDaoMock);
		impl.setConfigurationService(configurationServiceMock);
	}

	public void testSave() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		roleDaoMock.save(e);
		EasyMock.replay(roleDaoMock);
		impl.save(e);
		EasyMock.verify(roleDaoMock);
	}

	public void testDelete() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		roleDaoMock.delete(e);
		EasyMock.replay(roleDaoMock);
		impl.delete(e);
		EasyMock.verify(roleDaoMock);
	}

	public void testFindAll() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(impl.newRoleEntity());
		list.add(impl.newRoleEntity());
		EasyMock.expect(roleDaoMock.findAll()).andReturn(list);
		EasyMock.replay(roleDaoMock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(roleDaoMock);
	}

	public void testFindById() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		EasyMock.expect(roleDaoMock.findById(1)).andReturn(e);
		EasyMock.replay(roleDaoMock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(roleDaoMock);
	}

	public void testNewRoleEntity() {
		assertNotNull(impl.newRoleEntity());
	}

	public void testGetDefaultRegistrationRole() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		EasyMock.expect(configurationServiceMock.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE)).andReturn(1);
		EasyMock.expect(roleDaoMock.findById(1)).andReturn(e);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(roleDaoMock);
		RoleEntity defaultRegistrationRole = impl.findDefaultRegistrationRole();
		assertNotNull(defaultRegistrationRole);
		assertEquals(e.getId(), defaultRegistrationRole.getId());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(roleDaoMock);
	}

	public void testFindAllOrderByDescription() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(impl.newRoleEntity());
		list.add(impl.newRoleEntity());
		EasyMock.expect(roleDaoMock.findAllOrderByDescription()).andReturn(list);
		EasyMock.replay(roleDaoMock);
		assertEquals(list, impl.findAllOrderByDescription());
		EasyMock.verify(roleDaoMock);
	}

	public void testFindGuestRole() {
		RoleEntity role = impl.newRoleEntity();
		EasyMock.expect(configurationServiceMock.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE)).andReturn(1);
		EasyMock.expect(roleDaoMock.findById(1)).andReturn(role);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(roleDaoMock);
		assertEquals(role, impl.findGuestRole());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(roleDaoMock);
	}
}
