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

import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.role.RoleConstants;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.dao.UserDao;
import org.devproof.portal.core.module.user.entity.UserEntity;

/**
 * @author Carsten Hufe
 */
public class UserServiceImpl implements UserService {
	private UserDao userDao;
	private RoleService roleService;
	private EmailService emailService;
	private ConfigurationService configurationService;

	@Override
	public long countUserForRole(final RoleEntity role) {
		return this.userDao.countUserForRole(role);
	}

	@Override
	public boolean existsUsername(final String username) {
		return this.userDao.existsUsername(username) > 0;
	}

	@Override
	public List<UserEntity> findUserByEmail(final String email) {
		return this.userDao.findUserByEmail(email);
	}

	@Override
	public UserEntity findUserBySessionId(final String sessionId) {
		return this.userDao.findUserBySessionId(sessionId);
	}

	@Override
	public UserEntity findUserByUsername(final String username) {
		return this.userDao.findUserByUsername(username);
	}

	@Override
	public List<UserEntity> findUserWithRight(final String right) {
		return this.userDao.findUserWithRight(right);
	}

	@Override
	public void delete(final UserEntity entity) {
		this.userDao.delete(entity);
	}

	@Override
	public List<UserEntity> findAll() {
		return this.userDao.findAll();
	}

	@Override
	public UserEntity findById(final Integer id) {
		return this.userDao.findById(id);
	}

	@Override
	public void save(final UserEntity entity) {
		this.userDao.save(entity);
	}

	@Override
	public UserEntity newUserEntity() {
		return new UserEntity();
	}

	@Override
	public boolean activateUser(final String username, final String activationCode) {
		UserEntity user = findUserByUsername(username);
		if (user != null && activationCode.equals(user.getConfirmationCode())) {
			user.setConfirmationApprovedAt(PortalUtil.now());
			user.setConfirmationCode(null);
			user.setConfirmed(true);
			save(user);
			return true;
		}
		return false;
	}

	@Override
	public void registerUser(final UserEntity user, final String password, final String url, final String confirmationCode) {
		user.setActive(Boolean.TRUE);
		user.setPasswordMD5(PortalUtil.generateMd5(password));
		user.setRegistrationDate(PortalUtil.now());
		user.setChangedAt(PortalUtil.now());
		user.setRole(this.roleService.findById(this.configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE)));
		if (this.configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)) {
			EmailPlaceholderBean placeholder = PortalUtil.getEmailPlaceHolderByUser(user);
			user.setConfirmationCode(confirmationCode);
			user.setConfirmationRequestedAt(PortalUtil.now());
			user.setConfirmed(false);
			placeholder.setConfirmationLink(url);
			this.emailService.sendEmail(this.configurationService.findAsInteger(UserConstants.CONF_REGISTRATION_EMAIL), placeholder);

			// send notification
			Integer templateId = this.configurationService.findAsInteger(UserConstants.CONF_NOTIFY_USER_REGISTRATION);
			List<UserEntity> notifyUsers = findUserWithRight("emailnotification.registered.user");
			for (UserEntity notifyUser : notifyUsers) {
				placeholder.setToUsername(notifyUser.getUsername());
				placeholder.setToFirstname(notifyUser.getFirstname());
				placeholder.setToLastname(notifyUser.getLastname());
				placeholder.setToEmail(notifyUser.getEmail());
				this.emailService.sendEmail(templateId, placeholder);
			}
		} else {
			// no confirmation required
			user.setConfirmed(true);
		}
		save(user);
	}

	@Override
	public void setNewPassword(final String username, final String newPassword) {
		UserEntity user = this.userDao.findUserByUsername(username);
		user.setPasswordMD5(PortalUtil.generateMd5(newPassword));
		user.setChangedAt(PortalUtil.now());
		user.setForgotPasswordCode(null);
		this.userDao.save(user);
	}

	public void setRoleService(final RoleService roleService) {
		this.roleService = roleService;
	}

	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
	}

	public void setConfigurationService(final ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setUserDao(final UserDao userDao) {
		this.userDao = userDao;
	}
}