/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.role.service;

import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.role.RoleConstants;
import org.devproof.portal.core.module.role.dao.RoleDao;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class RoleServiceImpl implements RoleService {
	private ConfigurationService configurationService;
	private RoleDao roleDao;

	@Override
	public RoleEntity newRoleEntity() {
		return new RoleEntity();
	}

	@Override
	public List<RoleEntity> findAllOrderByDescription() {
		return roleDao.findAllOrderByDescription();
	}

	@Override
	public void delete(RoleEntity entity) {
		roleDao.delete(entity);
	}

	@Override
	public List<RoleEntity> findAll() {
		return roleDao.findAll();
	}

	@Override
	public RoleEntity findById(Integer id) {
		return roleDao.findById(id);
	}

	@Override
	public void save(RoleEntity entity) {
		roleDao.save(entity);
	}

	@Override
	public RoleEntity findGuestRole() {
		Integer roleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE);
		RoleEntity role = findById(roleId);
		return role;
	}

	@Override
	public RoleEntity findDefaultRegistrationRole() {
		return findById(configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE));
	}

	@Required
	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
