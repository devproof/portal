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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.UserConstants;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.repository.UserRepository;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Carsten Hufe
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    private final Log logger = LogFactory.getLog(UserServiceImpl.class);
    private UserRepository userDao;
    private RoleService roleService;
    private EmailService emailService;
    private ConfigurationService configurationService;

    @Override
    public long countUserForRole(Role role) {
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
    public List<User> findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }

    @Override
    public User findUserBySessionId(String sessionId) {
        return userDao.findUserBySessionId(sessionId);
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public List<User> findUserWithRight(String right) {
        return userDao.findUserWithRight(right);
    }

    @Override
    public void delete(User entity) {
        userDao.delete(entity);
    }

    @Override
    public User findById(Integer id) {
        return userDao.findById(id);
    }

    @Override
    public void save(User entity) {
        if (entity.getRegistrationDate() == null) {
            entity.setRegistrationDate(PortalUtil.now());
        }
        entity.setChangedAt(PortalUtil.now());
        userDao.save(entity);
    }

    @Override
    public User newUserEntity() {
        return new User();
    }

    @Override
    public User findGuestUser() {
        Role guestRole = roleService.findGuestRole();
        User user = newUserEntity();
        user.setUsername(guestRole.getDescription());
        user.setRole(guestRole);
        user.setGuestRole(true);
        return user;
    }

    @Override
    public boolean activateUser(String username, String activationCode) {
        User user = findUserByUsername(username);
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
    public void registerUser(User user, UrlCallback urlCallback) {
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

    protected EmailPlaceholderBean generateEmailPlaceholderForConfirmation(User user, UrlCallback urlCallback) {
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
        emailService.sendEmail(configurationService.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL), placeholder);
    }

    protected void generateConfirmationCode(User user) {
        user.setConfirmationCode(generateCode());
        user.setConfirmationRequestedAt(PortalUtil.now());
        user.setConfirmed(false);
    }

    protected void sendEmailNotificationToAdmins(EmailPlaceholderBean placeholder) {
        Integer templateId = configurationService.findAsInteger(UserConstants.CONF_NOTIFY_USER_REGISTRATION);
        List<User> notifyUsers = findUserWithRight("emailnotification.registered.user");
        for (User notifyUser : notifyUsers) {
            placeholder.setToUsername(notifyUser.getUsername());
            placeholder.setToFirstname(notifyUser.getFirstname());
            placeholder.setToLastname(notifyUser.getLastname());
            placeholder.setToEmail(notifyUser.getEmail());
            emailService.sendEmail(templateId, placeholder);
        }
    }

    @Override
    public void saveNewPassword(String username, String newPassword) {
        User user = findUserByUsername(username);
        user.setPlainPassword(newPassword);
        user.setForgotPasswordCode(null);
        save(user);
    }

    @Override
    public User authentificate(String username, String password, String ipAddress) throws UserNotConfirmedException, AuthentificationFailedException {
        User user = findUserByUsername(username);
        logger.info("Authentificate user " + username);
        if (user != null && user.equalPassword(password)) {
            if (!user.getActive()) {
                logger.info("User account is inactive: " + username);
                throw new AuthentificationFailedException("user.account.inactivated");
            } else if (!user.getRole().getActive()) {
                logger.info("User account role is inactive: " + username);
                throw new AuthentificationFailedException("user.role.inactivated");
            } else if (!user.getConfirmed()) {
                logger.info("User is not confirmed: " + username);
                throw new UserNotConfirmedException();
            }
            user.setLastIp(ipAddress);
            user.setLastLoginAt(PortalUtil.now());
            user.setSessionId(generateCode());
            save(user);
            return user;
        }
        logger.info("Invalid user password: " + username);
        throw new AuthentificationFailedException("user.password.not.found");
    }

    @Override
    public User authentificate(String sessionId, String ipAddress) {
        User user = findUserBySessionId(sessionId);
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
        List<User> users = generateForgotPasswordCode(usernameOrEmail);
        for (User user : users) {
            EmailPlaceholderBean placeholder = generateEmailPlaceholderForLostPassword(user, urlCallback);
            sendForgotPasswordEmail(placeholder);
        }
    }

    protected EmailPlaceholderBean generateEmailPlaceholderForLostPassword(User user, UrlCallback urlCallback) {
        EmailPlaceholderBean placeholder = PortalUtil.createEmailPlaceHolderByUser(user);
        placeholder.setResetPasswordLink(urlCallback.getUrl(user.getForgotPasswordCode()));
        return placeholder;
    }

    protected void sendForgotPasswordEmail(EmailPlaceholderBean placeholder) {
        emailService.sendEmail(configurationService.findAsInteger(UserConstants.CONF_PASSWORDFORGOT_EMAIL), placeholder);
    }

    protected List<User> generateForgotPasswordCode(String usernameOrEmail) {
        User userByName = findUserByUsername(usernameOrEmail);
        List<User> users = new ArrayList<User>();
        if (userByName != null) {
            users.add(userByName);
        } else {
            users = findUserByEmail(usernameOrEmail);
        }
        for (User user : users) {
            user.setForgotPasswordCode(generateCode());
            save(user);
        }
        return users;
    }

    @Override
    public void resendConfirmationCode(User user, UrlCallback urlCallback) {
        generateConfirmationCode(user);
        EmailPlaceholderBean placeholder = generateEmailPlaceholderForConfirmation(user, urlCallback);
        resendConfirmationEmail(placeholder);
        save(user);
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Autowired
    public void setUserDao(UserRepository userDao) {
        this.userDao = userDao;
    }
}