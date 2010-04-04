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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;

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
		userDaoMock = createStrictMock(UserDao.class);
		roleServiceMock = createMock(RoleService.class);
		emailServiceMock = createMock(EmailService.class);
		configurationServiceMock = createMock(ConfigurationService.class);
		impl = new UserServiceImpl();
		impl.setUserDao(userDaoMock);
		impl.setRoleService(roleServiceMock);
		impl.setEmailService(emailServiceMock);
		impl.setConfigurationService(configurationServiceMock);
	}

	public void testSave() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		expect(userDaoMock.save(e)).andReturn(e);
		replay(userDaoMock);
		impl.save(e);
		verify(userDaoMock);
	}

	public void testDelete() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		userDaoMock.delete(e);
		replay(userDaoMock);
		impl.delete(e);
		verify(userDaoMock);
	}

	public void testFindById() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		expect(userDaoMock.findById(1)).andReturn(e);
		replay(userDaoMock);
		assertEquals(impl.findById(1), e);
		verify(userDaoMock);
	}

	public void testNewUserEntity() {
		assertNotNull(impl.newUserEntity());
	}

	public void testCountUserForRole() {
		RoleEntity role = createTestRole();
		expect(userDaoMock.countUserForRole(role)).andReturn(4l);
		replay(userDaoMock);
		assertEquals(impl.countUserForRole(role), 4l);
		verify(userDaoMock);
	}

	public void testExistsUsername() {
		expect(userDaoMock.existsUsername("username")).andReturn(1l);
		replay(userDaoMock);
		assertTrue(impl.existsUsername("username"));
		verify(userDaoMock);
	}

	public void testFindUserByEmail() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		expect(userDaoMock.findUserByEmail("email@email.org")).andReturn(list);
		replay(userDaoMock);
		assertEquals(impl.findUserByEmail("email@email.org"), list);
		verify(userDaoMock);
	}

	public void testFindUserBySessionId() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		expect(userDaoMock.findUserBySessionId("12345")).andReturn(e);
		replay(userDaoMock);
		assertEquals(impl.findUserBySessionId("12345"), e);
		verify(userDaoMock);
	}

	public void testFindUserByUsername() {
		UserEntity e = impl.newUserEntity();
		e.setId(1);
		e.setUsername("username");
		expect(userDaoMock.findUserByUsername(e.getUsername())).andReturn(e);
		replay(userDaoMock);
		assertEquals(impl.findUserByUsername(e.getUsername()), e);
		verify(userDaoMock);
	}

	public void testFindUserWithRight() {
		List<UserEntity> list = new ArrayList<UserEntity>();
		list.add(impl.newUserEntity());
		list.add(impl.newUserEntity());
		expect(userDaoMock.findUserWithRight("right")).andReturn(list);
		replay(userDaoMock);
		assertEquals(impl.findUserWithRight("right"), list);
		verify(userDaoMock);
	}

	public void testFindGuestUser() {
		RoleEntity role = createTestRole();
		role.setDescription("Test Guest");
		expect(roleServiceMock.findGuestRole()).andReturn(role);
		replay(userDaoMock);
		replay(roleServiceMock);
		assertEquals(impl.findGuestUser().getUsername(), "Test Guest");
		verify(userDaoMock);
		verify(roleServiceMock);
	}

	public void testActivateUser() {
		UserEntity user = createTestUser(false);
		user.setConfirmationCode("right");
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(user).anyTimes();
		expect(userDaoMock.save(user)).andReturn(user);
		replay(userDaoMock);
		assertFalse(impl.activateUser("testuser", "wrong"));
		assertTrue(impl.activateUser("testuser", "right"));
		assertTrue(user.getConfirmed());
		assertNull(user.getConfirmationCode());
		assertNotNull(user.getConfirmationApprovedAt());
		verify(userDaoMock);
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
		expect(roleServiceMock.findDefaultRegistrationRole()).andReturn(createTestRole());
		replay(roleServiceMock);
		// only test the call order
		UserEntity user = createTestUser(false);
		impl.registerUser(user, createUrlCallback());
		assertEquals("123456", callOrder.toString());
		verify(roleServiceMock);
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
		expect(configurationServiceMock.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)).andReturn(Boolean.TRUE);
		replay(configurationServiceMock);
		assertTrue(impl.isConfirmationRequired());
		verify(configurationServiceMock);
	}

	public void testSendConfirmationEmail() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		expect(configurationServiceMock.findAsInteger(UserConstants.CONF_REGISTRATION_EMAIL)).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		replay(configurationServiceMock);
		replay(emailServiceMock);
		impl.sendConfirmationEmail(placeholder);
		assertEquals("http://url", placeholder.getConfirmationLink());
		verify(configurationServiceMock);
		verify(emailServiceMock);
	}

	public void testResendConfirmationEmail() {
		UserEntity user = createTestUser(false);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		expect(configurationServiceMock.findAsInteger(UserConstants.CONF_RECONFIRMATION_EMAIL)).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		replay(configurationServiceMock);
		replay(emailServiceMock);
		impl.resendConfirmationEmail(placeholder);
		assertEquals("http://url", placeholder.getConfirmationLink());
		verify(configurationServiceMock);
		verify(emailServiceMock);
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
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		replay(userDaoMock);
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
		verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_notConfirmed() {
		UserEntity expectedUser = createTestUser(false);
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		replay(userDaoMock);
		try {
			impl.authentificate("testuser", "password", "123.123.123.123");
			fail("User is not confirmed, so UserNotConfirmedException must be thrown");
		} catch (UserNotConfirmedException e) {

		} catch (AuthentificationFailedException e) {
			fail("Authentification failed");
		}
		verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_wrongUsername() {
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(null);
		replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_wrongPassword() {
		UserEntity expectedUser = createTestUser(true);
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_userInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.setActive(Boolean.FALSE);
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		verify(userDaoMock);
	}

	public void testAuthentificate_withUsername_roleInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.getRole().setActive(Boolean.FALSE);
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		replay(userDaoMock);
		try {
			impl.authentificate("testuser", "wrongpassword", "123.123.123.123");
			fail("Authentification successful, but failure expected");
		} catch (UserNotConfirmedException e) {
			fail("User is confirmed");
		} catch (AuthentificationFailedException e) {
		}
		verify(userDaoMock);
	}

	public void testAuthentificate_withSessionId_success() {
		UserEntity expectedUser = createTestUser(true);
		expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		replay(userDaoMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedUser, user);
		assertEquals("123.123.123.123", user.getLastIp());
		assertNotSame("sessionId", user.getSessionId());
		verify(userDaoMock);
	}

	public void testAuthentificate_withSessionId_nonExistingSessionId() {
		RoleEntity expectedGuestRole = createTestRole();
		expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(null);
		expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		verify(userDaoMock, roleServiceMock);
	}

	public void testAuthentificate_withSessionId_userInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.setActive(Boolean.FALSE);
		RoleEntity expectedGuestRole = createTestRole();
		expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		verify(userDaoMock, roleServiceMock);
	}

	public void testAuthentificate_withSessionId_roleInactive() {
		UserEntity expectedUser = createTestUser(true);
		expectedUser.getRole().setActive(Boolean.FALSE);
		RoleEntity expectedGuestRole = createTestRole();
		expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		verify(userDaoMock, roleServiceMock);
	}

	public void testAuthentificate_withSessionId_notConfirmed() {
		UserEntity expectedUser = createTestUser(false);
		RoleEntity expectedGuestRole = createTestRole();
		expect(userDaoMock.findUserBySessionId("sessionId")).andReturn(expectedUser);
		expect(roleServiceMock.findGuestRole()).andReturn(expectedGuestRole);
		replay(userDaoMock, roleServiceMock);
		UserEntity user = impl.authentificate("sessionId", "123.123.123.123");
		assertNotNull(user);
		assertEquals(expectedGuestRole, user.getRole());
		assertTrue(user.isGuestRole());
		verify(userDaoMock, roleServiceMock);
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
		expect(configurationServiceMock.findAsInteger(UserConstants.CONF_PASSWORDFORGOT_EMAIL)).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		replay(configurationServiceMock);
		replay(emailServiceMock);
		impl.sendForgotPasswordEmail(placeholder);
		assertEquals("http://url", placeholder.getResetPasswordLink());
		verify(configurationServiceMock);
		verify(emailServiceMock);
	}

	public void testGenerateForgotPasswordCode_withUsername() {
		UserEntity expectedUser = createTestUser(true);
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(expectedUser);
		expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		replay(userDaoMock);
		List<UserEntity> users = impl.generateForgotPasswordCode("testuser");
		UserEntity user = users.get(0);
		assertEquals(expectedUser, user);
		assertNotNull(user.getForgotPasswordCode());
		verify(userDaoMock);
	}

	public void testGenerateForgotPasswordCode_withEmail() {
		UserEntity expectedUser = createTestUser(true);
		expect(userDaoMock.findUserByUsername("max.power@no.domain")).andReturn(null);
		expect(userDaoMock.findUserByEmail("max.power@no.domain")).andReturn(Arrays.asList(expectedUser));
		expect(userDaoMock.save(expectedUser)).andReturn(expectedUser);
		replay(userDaoMock);
		List<UserEntity> users = impl.generateForgotPasswordCode("max.power@no.domain");
		UserEntity user = users.get(0);
		assertEquals(expectedUser, user);
		assertNotNull(user.getForgotPasswordCode());
		verify(userDaoMock);
	}

	public void testSendEmailNotificationToAdmins() {
		UserEntity user = createTestUser(false);
		List<UserEntity> users = new ArrayList<UserEntity>();
		users.add(user);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceholderForConfirmation(user, createUrlCallback());
		expect(configurationServiceMock.findAsInteger((String) anyObject())).andReturn(5);
		expect(userDaoMock.findUserWithRight("emailnotification.registered.user")).andReturn(users);
		emailServiceMock.sendEmail(5, placeholder);
		replay(configurationServiceMock);
		replay(userDaoMock);
		replay(emailServiceMock);
		impl.sendEmailNotificationToAdmins(placeholder);
		assertEquals(user.getFirstname(), placeholder.getFirstname());
		assertEquals(user.getLastname(), placeholder.getLastname());
		assertEquals(user.getEmail(), placeholder.getEmail());
		assertEquals(user.getUsername(), placeholder.getUsername());
		verify(configurationServiceMock);
		verify(userDaoMock);
		verify(emailServiceMock);
	}

	public void testSetNewPassword() {
		UserEntity user = createTestUser(false);
		expect(userDaoMock.findUserByUsername("testuser")).andReturn(user);
		expect(userDaoMock.save(user)).andReturn(user);
		replay(userDaoMock);
		impl.saveNewPassword("testuser", "12345");
		assertNull(user.getForgotPasswordCode());
		assertEquals(PortalUtil.generateMd5("12345"), user.getEncryptedPassword());
		verify(userDaoMock);
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
