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
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.role.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
    private ConfigurationService configurationService;
    private RoleRepository roleRepository;

    @Override
    public Role newRoleEntity() {
        return new Role();
    }

    @Override
    public List<Role> findAllOrderByDescription() {
        return roleRepository.findAllOrderByDescription();
    }

    @Override
    public void delete(Role entity) {
        roleRepository.delete(entity);
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Role findById(Integer id) {
        return roleRepository.findById(id);
    }

    @Override
    public void save(Role entity) {
        roleRepository.save(entity);
    }

    @Override
    public Role findGuestRole() {
        Integer roleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE);
        return findById(roleId);
    }

    @Override
    public Role findDefaultRegistrationRole() {
        return findById(configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE));
    }

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
