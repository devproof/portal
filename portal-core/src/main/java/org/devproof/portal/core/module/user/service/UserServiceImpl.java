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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.dao.UserDao;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class UserServiceImpl implements UserService {
	private static final Log LOG = LogFactory.getLog(UserServiceImpl.class);
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
	public UserEntity findById(Integer id) {
		return userDao.findById(id);
	}

	@Override
	public void save(UserEntity entity) {
		if (entity.getRegistrationDate() == null) {
			entity.setRegistrationDate(PortalUtil.now());
		}
		entity.setChangedAt(PortalUtil.now());
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
	public void registerUser(UserEntity user, UrlCallback urlCallback) {
		user.setActive(Boolean.TRUE);
		user.setRole(roleService.findDefaultRegistrationRole());
		if (isConfirmationRequired()) {
			generateConfirmationCode(user);
			EmailPlaceholderBean placeholder = generateEmailPlaceholderForConfirmation(user, urlCallback);
			sendConfirmationEmail(placeholder);
			sendEmailNotificationToAdmins(placeholder);
		} else {
			// no confirmation required
			user.setConfirmed(true);
		}
		save(user);
	}

	protected EmailPlaceholderBean generateEmailPlaceholderForConfirmation(UserEntity user, UrlCallback urlCallback) {
		EmailPlaceholderBean placeholder = PortalUtil.createEmailPlaceHolderByUser(user);
		placeholder.setConfirmationLink(urlCallback.getUrl(user.getConfirmationCode()));
		return placeholder;
	}

	protected Boolean isConfirmationRequired() {
		return configurationService.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION);
	}

	protected void sendConfirmationEmail(EmailPlaceholderBean placeholder) {
		emailService.sendEmail(configurationService.findAsInteger(UserConstants.CONF_REGISTRATION_EMAIL), placeholder);
	}

	protected void resendConfirmationEmail(EmailPlaceholderBean placeholder) {
		emailService
				.sendEmail(configurationService.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL), placeholder);
	}

	protected void generateConfirmationCode(UserEntity user) {
		user.setConfirmationCode(generateCode());
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
	public void saveNewPassword(String username, String newPassword) {
		UserEntity user = findUserByUsername(username);
		user.setPlainPassword(newPassword);
		user.setForgotPasswordCode(null);
		save(user);
	}

	@Override
	public UserEntity authentificate(String username, String password, String ipAddress)
			throws UserNotConfirmedException, AuthentificationFailedException {
		UserEntity user = findUserByUsername(username);
		LOG.info("Authentificate user " + username);
		if (user != null && user.equalPassword(password)) {
			if (!user.getActive()) {
				LOG.info("User account is inactive: " + username);
				throw new AuthentificationFailedException("user.account.inactivated");
			} else if (!user.getRole().getActive()) {
				LOG.info("User account role is inactive: " + username);
				throw new AuthentificationFailedException("user.role.inactivated");
			} else if (!user.getConfirmed()) {
				LOG.info("User is not confirmed: " + username);
				throw new UserNotConfirmedException();
			}
			user.setLastIp(ipAddress);
			user.setLastLoginAt(PortalUtil.now());
			user.setSessionId(generateCode());
			save(user);
			return user;
		}
		LOG.info("Invalid user password: " + username);
		throw new AuthentificationFailedException("user.password.not.found");
	}

	@Override
	public UserEntity authentificate(String sessionId, String ipAddress) {
		UserEntity user = findUserBySessionId(sessionId);
		if (user != null && user.getActive() && user.getRole().getActive() && user.getConfirmed()) {
			user.setLastIp(ipAddress);
			user.setLastLoginAt(PortalUtil.now());
			user.setSessionId(generateCode());
			save(user);
			return user;
		}
		return findGuestUser();
	}

	protected String generateCode() {
		return UUID.randomUUID().toString();
	}

	@Override
	public void sendForgotPasswordCode(String usernameOrEmail, UrlCallback urlCallback) {
		List<UserEntity> users = generateForgotPasswordCode(usernameOrEmail);
		for (UserEntity user : users) {
			EmailPlaceholderBean placeholder = generateEmailPlaceholderForLostPassword(user, urlCallback);
			sendForgotPasswordEmail(placeholder);
		}
	}

	protected EmailPlaceholderBean generateEmailPlaceholderForLostPassword(UserEntity user, UrlCallback urlCallback) {
		EmailPlaceholderBean placeholder = PortalUtil.createEmailPlaceHolderByUser(user);
		placeholder.setResetPasswordLink(urlCallback.getUrl(user.getForgotPasswordCode()));
		return placeholder;
	}

	protected void sendForgotPasswordEmail(EmailPlaceholderBean placeholder) {
		emailService
				.sendEmail(configurationService.findAsInteger(UserConstants.CONF_PASSWORDFORGOT_EMAIL), placeholder);
	}

	protected List<UserEntity> generateForgotPasswordCode(String usernameOrEmail) {
		UserEntity userByName = findUserByUsername(usernameOrEmail);
		List<UserEntity> users = new ArrayList<UserEntity>();
		if (userByName != null) {
			users.add(userByName);
		} else {
			users = findUserByEmail(usernameOrEmail);
		}
		for (UserEntity user : users) {
			user.setForgotPasswordCode(generateCode());
			save(user);
		}
		return users;
	}

	@Override
	public void resendConfirmationCode(UserEntity user, UrlCallback urlCallback) {
		generateConfirmationCode(user);
		EmailPlaceholderBean placeholder = generateEmailPlaceholderForConfirmation(user, urlCallback);
		resendConfirmationEmail(placeholder);
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