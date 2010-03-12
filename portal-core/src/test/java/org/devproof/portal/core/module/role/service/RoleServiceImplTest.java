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
package org.devproof.portal.core.module.role.service;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.role.RoleConstants;
import org.devproof.portal.core.module.role.dao.RoleDao;
import org.devproof.portal.core.module.role.entity.RoleEntity;

/**
 * @author Carsten Hufe
 */
public class RoleServiceImplTest extends TestCase {
	private RoleServiceImpl impl;
	private RoleDao roleDaoMock;
	private ConfigurationService configurationServiceMock;

	@Override
	public void setUp() throws Exception {
		roleDaoMock = createStrictMock(RoleDao.class);
		configurationServiceMock = createStrictMock(ConfigurationService.class);
		impl = new RoleServiceImpl();
		impl.setRoleDao(roleDaoMock);
		impl.setConfigurationService(configurationServiceMock);
	}

	public void testSave() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		expect(roleDaoMock.save(e)).andReturn(e);
		replay(roleDaoMock);
		impl.save(e);
		verify(roleDaoMock);
	}

	public void testDelete() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		roleDaoMock.delete(e);
		replay(roleDaoMock);
		impl.delete(e);
		verify(roleDaoMock);
	}

	public void testFindAll() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(impl.newRoleEntity());
		list.add(impl.newRoleEntity());
		expect(roleDaoMock.findAll()).andReturn(list);
		replay(roleDaoMock);
		assertEquals(list, impl.findAll());
		verify(roleDaoMock);
	}

	public void testFindById() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		expect(roleDaoMock.findById(1)).andReturn(e);
		replay(roleDaoMock);
		assertEquals(impl.findById(1), e);
		verify(roleDaoMock);
	}

	public void testNewRoleEntity() {
		assertNotNull(impl.newRoleEntity());
	}

	public void testGetDefaultRegistrationRole() {
		RoleEntity e = impl.newRoleEntity();
		e.setId(1);
		expect(configurationServiceMock.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE)).andReturn(1);
		expect(roleDaoMock.findById(1)).andReturn(e);
		replay(configurationServiceMock);
		replay(roleDaoMock);
		RoleEntity defaultRegistrationRole = impl.findDefaultRegistrationRole();
		assertNotNull(defaultRegistrationRole);
		assertEquals(e.getId(), defaultRegistrationRole.getId());
		verify(configurationServiceMock);
		verify(roleDaoMock);
	}

	public void testFindAllOrderByDescription() {
		List<RoleEntity> list = new ArrayList<RoleEntity>();
		list.add(impl.newRoleEntity());
		list.add(impl.newRoleEntity());
		expect(roleDaoMock.findAllOrderByDescription()).andReturn(list);
		replay(roleDaoMock);
		assertEquals(list, impl.findAllOrderByDescription());
		verify(roleDaoMock);
	}

	public void testFindGuestRole() {
		RoleEntity role = impl.newRoleEntity();
		expect(configurationServiceMock.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE)).andReturn(1);
		expect(roleDaoMock.findById(1)).andReturn(role);
		replay(configurationServiceMock);
		replay(roleDaoMock);
		assertEquals(role, impl.findGuestRole());
		verify(configurationServiceMock);
		verify(roleDaoMock);
	}
}
