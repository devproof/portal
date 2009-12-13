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
package org.devproof.portal.core.module.user.service;

import java.util.List;

import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.dao.UserDao;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class UserServiceImpl implements UserService {
	private UserDao userDao;
	private RoleService roleService;
	private EmailService emailService;
	private ConfigurationService configurationService;

	@Override
	public long countUserForRole(RoleEntity role) {
		return userDao.countUserForRole(role);
	}

	@Override
	public boolean existsUsername(String username) {
		if (UserConstants.UNKNOWN_USERNAME.equalsIgnoreCase(username)) {
			return true;
		}
		return userDao.existsUsername(username) > 0;
	}

	@Override
	public List<UserEntity> findUserByEmail(String email) {
		return userDao.findUserByEmail(email);
	}

	@Override
	public UserEntity findUserBySessionId(String sessionId) {
		return userDao.findUserBySessionId(sessionId);
	}

	@Override
	public UserEntity findUserByUsername(String username) {
		return userDao.findUserByUsername(username);
	}

	@Override
	public List<UserEntity> findUserWithRight(String right) {
		return userDao.findUserWithRight(right);
	}

	@Override
	public void delete(UserEntity entity) {
		userDao.delete(entity);
	}

	@Override
	public List<UserEntity> findAll() {
		return userDao.findAll();
	}

	@Override
	public UserEntity findById(Integer id) {
		return userDao.findById(id);
	}

	@Override
	public void save(UserEntity entity) {
		userDao.save(entity);
	}

	@Override
	public UserEntity newUserEntity() {
		return new UserEntity();
	}

	@Override
	public UserEntity findGuestUser() {
		RoleEntity guestRole = roleService.findGuestRole();
		UserEntity user = newUserEntity();
		user.setUsername(guestRole.getDescription());
		user.setRole(guestRole);
		user.setGuestRole(true);
		return user;
	}

	@Override
	public boolean activateUser(String username, String activationCode) {
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
	public void registerUser(UserEntity user, String password, String url, String confirmationCode) {
		setUserRegistrationValues(user, password);
		if (isConfirmationRequired()) {
			EmailPlaceholderBean placeholder = generateEmailPlaceHolder(user);
			setConfirmationCode(user, confirmationCode);
			sendConfirmationEmail(url, placeholder);
			sendEmailNotificationToAdmins(placeholder);
		} else {
			// no confirmation required
			user.setConfirmed(true);
		}
		save(user);
	}

	protected EmailPlaceholderBean generateEmailPlaceHolder(UserEntity user) {
		return PortalUtil.getEmailPlaceHolderByUser(user);
	}

	protected void setUserRegistrationValues(UserEntity user, String password) {
		user.setActive(Boolean.TRUE);
		user.setPasswordMD5(PortalUtil.generateMd5(password));
		user.setRegistrationDate(PortalUtil.now());
		user.setChangedAt(PortalUtil.now());
		user.setRole(roleService.findDefaultRegistrationRole());
	}

	protected Boolean isConfirmationRequired() {
		return configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION);
	}

	protected void sendConfirmationEmail(String url, EmailPlaceholderBean placeholder) {
		placeholder.setConfirmationLink(url);
		emailService.sendEmail(configurationService.findAsInteger(UserConstants.CONF_REGISTRATION_EMAIL), placeholder);
	}

	protected void setConfirmationCode(UserEntity user, String confirmationCode) {
		user.setConfirmationCode(confirmationCode);
		user.setConfirmationRequestedAt(PortalUtil.now());
		user.setConfirmed(false);
	}

	protected void sendEmailNotificationToAdmins(EmailPlaceholderBean placeholder) {
		Integer templateId = configurationService.findAsInteger(UserConstants.CONF_NOTIFY_USER_REGISTRATION);
		List<UserEntity> notifyUsers = findUserWithRight("emailnotification.registered.user");
		for (UserEntity notifyUser : notifyUsers) {
			placeholder.setToUsername(notifyUser.getUsername());
			placeholder.setToFirstname(notifyUser.getFirstname());
			placeholder.setToLastname(notifyUser.getLastname());
			placeholder.setToEmail(notifyUser.getEmail());
			emailService.sendEmail(templateId, placeholder);
		}
	}

	@Override
	public void setNewPassword(String username, String newPassword) {
		UserEntity user = findUserByUsername(username);
		user.setPasswordMD5(PortalUtil.generateMd5(newPassword));
		user.setChangedAt(PortalUtil.now());
		user.setForgotPasswordCode(null);
		save(user);
	}

	@Required
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	@Required
	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Required
	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	@Required
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}