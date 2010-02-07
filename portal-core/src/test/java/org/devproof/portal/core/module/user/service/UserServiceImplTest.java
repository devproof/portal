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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

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
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class UserServiceImplTest extends TestCase {
	private UserServiceImpl impl;
	private UserDao userDaoMock;
	private RoleService roleServiceMock;
	private EmailService emailServiceMock;
	private ConfigurationService configurationServiceMock;

	@Override
	public void setUp() throws Exception {
		userDaoMock = EasyMock.createStrictMock(UserDao.class);
		roleServiceMock = EasyMock.createMock(RoleService.class);
		emailServiceMock = EasyMock.createMock(EmailService.class);
		configurationServiceMock = EasyMock.createMock(ConfigurationService.class);
		impl = new UserServiceImpl();
		impl.setUserDao(userDaoMock);
		impl.setRoleService(roleServiceMock);
		impl.setEmailService(emailServiceMock);
		impl.setConfigurationService(configurationServiceMock);
	}

	public void testSave() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(userDaoMock.save(e)).andReturn(e);
		EasyMock.replay(userDaoMock);
		impl.save(e);
		EasyMock.verify(userDaoMock);
	}

	public void testDelete() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		userDaoMock.delete(e);
		EasyMock.replay(userDaoMock);
		impl.delete(e);
		EasyMock.verify(userDaoMock);
	}

	public void testFindAll() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		EasyMock.expect(userDaoMock.findAll()).andReturn(list);
		EasyMock.replay(userDaoMock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(userDaoMock);
	}

	public void testFindById() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(userDaoMock.findById(1)).andReturn(e);
		EasyMock.replay(userDaoMock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(userDaoMock);
	}

	public void testNewUserEntity() {
		assertNotNull(impl.newUserEntity());
	}

	public void testCountUserForRole() {
		RoleEntity role = createTestRole();
		EasyMock.expect(userDaoMock.countUserForRole(role)).andReturn(4l);
		EasyMock.replay(userDaoMock);
		assertEquals(impl.countUserForRole(role), 4l);
		EasyMock.verify(userDaoMock);
	}

	public void testExistsUsername() {
		EasyMock.expect(userDaoMock.existsUsername("username")).andReturn(1l);
		EasyMock.replay(userDaoMock);
		assertTrue(impl.existsUsername("username"));
		EasyMock.verify(userDaoMock);
	}

	public void testFindUserByEmail() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		EasyMock.expect(userDaoMock.findUserByEmail("email@email.org")).andReturn(list);
		EasyMock.replay(userDaoMock);
		assertEquals(impl.findUserByEmail("email@email.org"), list);
		EasyMock.verify(userDaoMock);
	}

	public void testFindUserBySessionId() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		EasyMock.expect(userDaoMock.findUserBySessionId("12345")).andReturn(e);
		EasyMock.replay(userDaoMock);
		assertEquals(impl.findUserBySessionId("12345"), e);
		EasyMock.verify(userDaoMock);
	}

	public void testFindUserByUsername() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		e.setUsername("username");
		EasyMock.expect(userDaoMock.findUserByUsername(e.getUsername())).andReturn(e);
		EasyMock.replay(userDaoMock);
		assertEquals(impl.findUserByUsername(e.getUsername()), e);
		EasyMock.verify(userDaoMock);
	}

	public void testFindUserWithRight() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		EasyMock.expect(userDaoMock.findUserWithRight("right")).andReturn(list);
		EasyMock.replay(userDaoMock);
		assertEquals(impl.findUserWithRight("right"), list);
		EasyMock.verify(userDaoMock);
	}

	public void testFindGuestUser() {
		RoleEntity role = createTestRole();
		role.setDescription("Test Guest");
		EasyMock.expect(roleServiceMock.findGuestRole()).andReturn(role);
		EasyMock.replay(userDaoMock);
		EasyMock.replay(roleServiceMock);
		assertEquals(impl.findGuestUser().getUsername(), "Test Guest");
		EasyMock.verify(userDaoMock);
		EasyMock.verify(roleServiceMock);
	}

	public void testActivateUser() {
		UserEntity user = createTestUser(false);
		user.setConfirmationCode("right");
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(user).anyTimes();
		EasyMock.expect(userDaoMock.save(user)).andReturn(user);
		EasyMock.replay(userDaoMock);
		assertFalse(impl.activateUser("testuser", "wrong"));
		assertTrue(impl.activateUser("testuser", "right"));
		assertTrue(user.getConfirmed());
		assertNull(user.getConfirmationCode());
		assertNotNull(user.getConfirmationApprovedAt());
		EasyMock.verify(userDaoMock);
	}

	public void testRegisterUser() {
		final StringBuilder callOrder = new StringBuilder();
		impl = new UserServiceImpl() {
			@Override
			protected Boolean isConfirmationRequired() {
				callOrder.append("1");
				return true;
			}

			@Override
			protected void generateConfirmationCode(UserEntity user) {
				callOrder.append("2");
			}

			@Override
			protected EmailPlaceholderBean generateEmailPlaceholderForConfirmation(UserEntity user, UrlCallback callback) {
				callOrder.append("3");
				return null;
			}

			@Override
			protected void sendConfirmationEmail(EmailPlaceholderBean placeholder) {
				callOrder.append("4");
			}

			@Override
			protected void sendEmailNotificationToAdmins(EmailPlaceholderBean placeholder) {
				callOrder.append("5");
			}

			@Override
			public void save(UserEntity user) {
				callOrder.append("6");
			}
		};
		impl.setRoleService(roleServiceMock);
		EasyMock.expect(roleServiceMock.findDefaultRegistrationRole()).andReturn(createTestRole());
		EasyMock.replay(roleServiceMock);
		// only test the call order
		UserEntity user = createTestUser(false);
		impl.registerUser(user, createUrlCallback());
		assertEquals("123456", callOrder.toString());
		EasyMock.verify(roleServiceMock);
	}

	private UrlCallback createUrlCallback() {
		return new UrlCallback() {
			@Override
			public String getUrl(String generatedCode) {
				return "http://url";
			}
		};
	}

	public void testGenerateEmailPlaceholderForConfirmation() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		assertEquals("http://url", placeholder.getConfirmationLink());
		assertEquals("testuser", placeholder.getUsername());
		assertEquals("max.power@no.domain", placeholder.getEmail());
		assertEquals("max", placeholder.getFirstname());
		assertEquals("power", placeholder.getLastname());
		assertNotNull(placeholder.getBirthday());
	}

	public void testIsConfirmationRequired() {
		EasyMock.expect(configurationServiceMock.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)).andReturn(
				Boolean.TRUE);
		EasyMock.replay(configurationServiceMock);
		assertTrue(impl.isConfirmationRequired());
		EasyMock.verify(configurationServiceMock);
	}

	public void testSendConfirmationEmail() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		EasyMock.expect(configurationServiceMock.findAsInteger(UserConstants.CONF_REGISTRATION_EMAIL)).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(emailServiceMock);
		impl.sendConfirmationEmail(placeholder);
		assertEquals("http://url", placeholder.getConfirmationLink());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(emailServiceMock);
	}

	public void testResendConfirmationEmail() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		EasyMock.expect(configurationServiceMock.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL)).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(emailServiceMock);
		impl.resendConfirmationEmail(placeholder);
		assertEquals("http://url", placeholder.getConfirmationLink());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(emailServiceMock);
	}

	public void testResendConfirmationCode() {
		final StringBuilder callOrder = new StringBuilder();
		impl = new UserServiceImpl() {

			@Override
			protected void generateConfirmationCode(UserEntity user) {
				callOrder.append("1");
			}

			@Override
			protected EmailPlaceholderBean generateEmailPlaceholderForConfirmation(UserEntity user,
					UrlCallback urlCallback) {
				callOrder.append("2");
				return null;
			}

			@Override
			protected void resendConfirmationEmail(EmailPlaceholderBean placeholder) {
				callOrder.append("3");
			}

			@Override
			public void save(UserEntity entity) {
				callOrder.append("4");
			}
		};
		UserEntity user = createTestUser(false);
		impl.resendConfirmationCode(user, createUrlCallback());
		assertEquals("1234", callOrder.toString());
	}

	public void testGenerateConfirmationCode() {
		impl = new UserServiceImpl() {
			@Override
			protected String generateCode() {
				return "mockcode";
			}
		};
		UserEntity user = createTestUser(false);
		user.setConfirmed(true);
		impl.generateConfirmationCode(user);
		assertFalse(user.getConfirmed());
		assertEquals("mockcode", user.getConfirmationCode());
		assertNotNull(user.getConfirmationRequestedAt());
	}

	public void testGenerateCode() {
		assertNotNull(impl.generateCode());
	}

	public void testAuthentificate_withUsername_success() {
		UserEntity expectedUser = createTestUser(true);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		EasyMock.expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		try {
			UserEntity user = impl.authentificate("testuser", "password", "123.123.123.123");
			assertNotNull(user);
			assertEquals(expectedUser, user);
			assertEquals("123.123.123.123", user.getLastIp());
			assertNotNull(user.getSessionId());
		} catch (UserNotConfirmedException e) {
			fail("User not confirmed");
		} catch (AuthentificationFailedException e) {
			fail("Authentification failed");
		}
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_notConfirmed() {
		UserEntity expectedUser = createTestUser(false);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		try {
			impl.authentificate("testuser", "password", "123.123.123.123");
			fail("User is not confirmed, so UserNotConfirmedException must be thrown");
		} catch (UserNotConfirmedException e) {

		} catch (AuthentificationFailedException e) {
			fail("Authentification failed");
		}
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_wrongUsername() {
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(null);
		EasyMock.replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_wrongPassword() {
		UserEntity expectedUser = createTestUser(true);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_userInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.setActive(Boolean.FALSE);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_roleInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.getRole().setActive(Boolean.FALSE);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withSessionId_success() {
		UserEntity expectedUser = createTestUser(true);
		EasyMock.expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		EasyMock.expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedUser, user);
		assertEquals("123.123.123.123", user.getLastIp());
		assertNotSame("sessionId", user.getSessionId());
		EasyMock.verify(userDaoMock);
	}

	public void testAuthentificate_withSessionId_nonExistingSessionId() {
		RoleEntity expectedGuestRole = createTestRole();
		EasyMock.expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(null);
		EasyMock.expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		EasyMock.replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		EasyMock.verify(userDaoMock, roleServiceMock);
	}

	public void testAuthentificate_withSessionId_userInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.setActive(Boolean.FALSE);
		RoleEntity expectedGuestRole = createTestRole();
		EasyMock.expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		EasyMock.expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		EasyMock.replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		EasyMock.verify(userDaoMock, roleServiceMock);
	}

	public void testAuthentificate_withSessionId_roleInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.getRole().setActive(Boolean.FALSE);
		RoleEntity expectedGuestRole = createTestRole();
		EasyMock.expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		EasyMock.expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		EasyMock.replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		EasyMock.verify(userDaoMock, roleServiceMock);
	}

	public void testAuthentificate_withSessionId_notConfirmed() {
		UserEntity expectedUser = createTestUser(false);
		RoleEntity expectedGuestRole = createTestRole();
		EasyMock.expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		EasyMock.expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		EasyMock.replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		EasyMock.verify(userDaoMock, roleServiceMock);
	}

	public void testSendForgotPasswordCode() {
		final StringBuilder callOrder = new StringBuilder();
		impl = new UserServiceImpl() {
			@Override
			protected List<UserEntity> generateForgotPasswordCode(String usernameOrEmail) {
				callOrder.append("1");
				return Arrays.asList(createTestUser(true));
			}

			@Override
			protected EmailPlaceholderBean generateEmailPlaceholderForLostPassword(UserEntity user,
					UrlCallback urlCallback) {
				callOrder.append("2");
				return null;
			}

			@Override
			protected void sendForgotPasswordEmail(EmailPlaceholderBean placeholder) {
				callOrder.append("3");
			}
		};
		impl.sendForgotPasswordCode("testuser", createUrlCallback());
		assertEquals("123", callOrder.toString());
	}

	public void testGenerateEmailPlaceholderForLostPassword() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForLostPassword(user, createUrlCallback());
		assertEquals("http://url", placeholder.getResetPasswordLink());
		assertEquals("testuser", placeholder.getUsername());
		assertEquals("max.power@no.domain", placeholder.getEmail());
		assertEquals("max", placeholder.getFirstname());
		assertEquals("power", placeholder.getLastname());
		assertNotNull(placeholder.getBirthday());
	}

	public void testSendForgotPasswordEmail() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForLostPassword(user, createUrlCallback());
		EasyMock.expect(configurationServiceMock.findAsInteger(UserConstants.CONF_PASSWORDFORGOT_EMAIL)).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(emailServiceMock);
		impl.sendForgotPasswordEmail(placeholder);
		assertEquals("http://url", placeholder.getResetPasswordLink());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(emailServiceMock);
	}

	public void testGenerateForgotPasswordCode_withUsername() {
		UserEntity expectedUser = createTestUser(true);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		EasyMock.expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		List<UserEntity> users = impl.generateForgotPasswordCode("testuser");
		UserEntity user = users.get(0);
		assertEquals(expectedUser, user);
		assertNotNull(user.getForgotPasswordCode());
		EasyMock.verify(userDaoMock);
	}

	public void testGenerateForgotPasswordCode_withEmail() {
		UserEntity expectedUser = createTestUser(true);
		EasyMock.expect(userDaoMock.findUserByUsername("max.power@no.domain")).andReturn(null);
		EasyMock.expect(userDaoMock.findUserByEmail("max.power@no.domain")).andReturn(Arrays.asList(expectedUser));
		EasyMock.expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		EasyMock.replay(userDaoMock);
		List<UserEntity> users = impl.generateForgotPasswordCode("max.power@no.domain");
		UserEntity user = users.get(0);
		assertEquals(expectedUser, user);
		assertNotNull(user.getForgotPasswordCode());
		EasyMock.verify(userDaoMock);
	}

	public void testSendEmailNotificationToAdmins() {
		UserEntity user = createTestUser(false);
		List<UserEntity> users = new ArrayList<UserEntity>();
		users.add(user);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		EasyMock.expect(configurationServiceMock.findAsInteger((String) EasyMock.anyObject())).andReturn(5);
		EasyMock.expect(userDaoMock.findUserWithRight("emailnotification.registered.user")).andReturn(users);
		emailServiceMock.sendEmail(5, placeholder);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(userDaoMock);
		EasyMock.replay(emailServiceMock);
		impl.sendEmailNotificationToAdmins(placeholder);
		assertEquals(user.getFirstname(), placeholder.getFirstname());
		assertEquals(user.getLastname(), placeholder.getLastname());
		assertEquals(user.getEmail(), placeholder.getEmail());
		assertEquals(user.getUsername(), placeholder.getUsername());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(userDaoMock);
		EasyMock.verify(emailServiceMock);
	}

	public void testSetNewPassword() {
		UserEntity user = createTestUser(false);
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(user);
		EasyMock.expect(userDaoMock.save(user)).andReturn(user);
		EasyMock.replay(userDaoMock);
		impl.saveNewPassword("testuser", "12345");
		assertNull(user.getForgotPasswordCode());
		assertEquals(PortalUtil.generateMd5("12345"), user.getEncryptedPassword());
		EasyMock.verify(userDaoMock);
	}

	private UserEntity createTestUser(boolean confirmed) {
		UserEntity user = new UserEntity();
		user.setConfirmed(confirmed);
		user.setUsername("testuser");
		user.setFirstname("max");
		user.setLastname("power");
		user.setEmail("max.power@no.domain");
		user.setPlainPassword("password");
		user.setForgotPasswordCode("forgotcode");
		user.setBirthday(new Date());
		user.setRole(createTestRole());
		user.setSessionId("sessionId");
		return user;
	}

	private RoleEntity createTestRole() {
		RoleEntity role = new RoleEntity();
		role.setId(1);
		role.setActive(true);
		return role;
	}
}
