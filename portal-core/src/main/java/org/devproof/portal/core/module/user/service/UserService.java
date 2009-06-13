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

import java.util.List;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.user.entity.UserEntity;

/**
 * @author Carsten Hufe
 */
public interface UserService extends CrudService<UserEntity, Integer> {
	/**
	 * Returns a new instance of the UserEntity
	 */
	public UserEntity newUserEntity();

	/**
	 * Returns a user entity by the username
	 */
	public UserEntity findUserByUsername(String username);

	/**
	 * Returns a user entity by the session id
	 */
	public UserEntity findUserBySessionId(String sessionId);

	/**
	 * Returns a list with users by the given email
	 */
	public List<UserEntity> findUserByEmail(String email);

	/**
	 * Test wether a user exist
	 */
	public boolean existsUsername(String username);

	/**
	 * Returns the number of users for the given role
	 */
	public long countUserForRole(RoleEntity role);

	/**
	 * Returns all users with the given right
	 */
	public List<UserEntity> findUserWithRight(String right);

	/**
	 * Register a new user
	 */
	public void registerUser(UserEntity user, String password, String url, String confirmationCode);

	/**
	 * Activate user
	 * 
	 * @return true, if user was activated
	 */
	public boolean activateUser(String username, String activationCode);

	/**
	 * Sets a new password
	 */
	public void setNewPassword(String username, String newPassword);
}
