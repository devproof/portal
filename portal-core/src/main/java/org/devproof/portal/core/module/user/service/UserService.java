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
package org.devproof.portal.core.module.user.service;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public interface UserService extends CrudService<UserEntity, Integer> {
    /**
     * Returns a new instance of the UserEntity
     */
    UserEntity newUserEntity();

    /**
     * Returns a user entity by the username
     */
    UserEntity findUserByUsername(String username);

    /**
     * Returns a user entity by the session id
     */
    UserEntity findUserBySessionId(String sessionId);

    /**
     * Returns the guest user
     */
    UserEntity findGuestUser();

    /**
     * Returns a list with users by the given email
     */
    List<UserEntity> findUserByEmail(String email);

    /**
     * Test wether a user exist
     */
    boolean existsUsername(String username);

    /**
     * Returns the number of users for the given role
     */
    long countUserForRole(RoleEntity role);

    /**
     * Returns all users with the given right
     */
    List<UserEntity> findUserWithRight(String right);

    /**
     * Register a new user
     */
    void registerUser(UserEntity user, UrlCallback urlCallback);

    /**
     * Activate user
     *
     * @return true, if user was activated
     */
    boolean activateUser(String username, String activationCode);

    /**
     * Sets a new password
     */
    void saveNewPassword(String username, String newPassword);

    /**
     * Authentificates a user
     *
     * @param username  the username to login
     * @param password  the user password
     * @param ipAddress Current ip address (for logging)
     * @return returns the UserEntity when the authentification was successful
     * @throws UserNotConfirmedException thrown when a user is not confirmed
     * @throws AuthentificationFailedException
     *                                   thrown when an authentification failed e.g. wrong password
     */
    UserEntity authentificate(String username, String password, String ipAddress) throws UserNotConfirmedException, AuthentificationFailedException;

    /**
     * Re-Authentificates a user by tthe session id
     *
     * @param sessionId Session ID
     * @param ipAddress Current IP address for logging
     * @return the authentificated UserEntity, if the authentification failes it
     *         returns the guest user
     */
    UserEntity authentificate(String sessionId, String ipAddress);

    /**
     * Sends the code for the lost password
     *
     * @param usernameOrEmail username or email address
     * @param urlCallback     callback to build the URLs
     */
    void sendForgotPasswordCode(String usernameOrEmail, UrlCallback urlCallback);

    /**
     * Resends the confirmation code
     *
     * @param user        user
     * @param urlCallback callback to build the URLs
     */
	void resendConfirmationCode(UserEntity user, UrlCallback urlCallback);
}
