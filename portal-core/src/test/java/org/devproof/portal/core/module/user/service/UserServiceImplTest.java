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
		userDaoMock.save(e);
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
		RoleEntity role = new RoleEntity();
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
		RoleEntity role = new RoleEntity();
		role.setDescription("Test Guest");
		EasyMock.expect(roleServiceMock.findGuestRole()).andReturn(role);
		EasyMock.replay(userDaoMock);
		EasyMock.replay(roleServiceMock);
		assertEquals(impl.findGuestUser().getUsername(), "Test Guest");
		EasyMock.verify(userDaoMock);
		EasyMock.verify(roleServiceMock);
	}

	public void testActivateUser() {
		UserEntity user = createTestUser();
		user.setConfirmationCode("right");
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(user).anyTimes();
		userDaoMock.save(user);
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
			protected void setUserRegistrationValues(final UserEntity user, final String password) {
				callOrder.append("1");
			}

			@Override
			protected Boolean isConfirmationRequired() {
				callOrder.append("2");
				return true;
			}

			@Override
			protected EmailPlaceholderBean generateEmailPlaceHolder(final UserEntity user) {
				callOrder.append("3");
				return null;
			}

			@Override
			protected void setConfirmationCode(final UserEntity user, final String confirmationCode) {
				callOrder.append("4");
			}

			@Override
			protected void sendConfirmationEmail(final String url, final EmailPlaceholderBean placeholder) {
				callOrder.append("5");
			}

			@Override
			protected void sendEmailNotificationToAdmins(final EmailPlaceholderBean placeholder) {
				callOrder.append("6");
			}

			@Override
			public void save(final UserEntity user) {
				callOrder.append("7");
			}
		};
		// only test the call order
		UserEntity user = createTestUser();
		impl.registerUser(user, "password", "http://url", "xxx");
		assertEquals("1234567", callOrder.toString());
	}

	public void testSetUserRegistrationValues() {
		UserEntity user = createTestUser();
		RoleEntity role = new RoleEntity();
		role.setDescription("Test Reg");
		role.setId(1);
		EasyMock.expect(roleServiceMock.findDefaultRegistrationRole()).andReturn(role);
		EasyMock.replay(roleServiceMock);
		impl.setUserRegistrationValues(user, "12345");
		assertTrue(user.getActive());
		assertEquals(PortalUtil.generateMd5("12345"), user.getPasswordMD5());
		assertNotNull(user.getRegistrationDate());
		assertNotNull(user.getChangedAt());
		assertEquals(role, user.getRole());
		EasyMock.verify(roleServiceMock);
	}

	public void testIsConfirmationRequired() {
		EasyMock.expect(configurationServiceMock.findAsBoolean(UserConstants.CONF_EMAIL_VALIDATION)).andReturn(
				Boolean.TRUE);
		EasyMock.replay(configurationServiceMock);
		assertTrue(impl.isConfirmationRequired());
		EasyMock.verify(configurationServiceMock);
	}

	public void testSendConfirmationEmail() {
		UserEntity user = createTestUser();
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceHolder(user);
		EasyMock.expect(configurationServiceMock.findAsInteger((String) EasyMock.anyObject())).andReturn(5);
		emailServiceMock.sendEmail(5, placeholder);
		EasyMock.replay(configurationServiceMock);
		EasyMock.replay(emailServiceMock);
		impl.sendConfirmationEmail("http://url", placeholder);
		assertEquals("http://url", placeholder.getConfirmationLink());
		EasyMock.verify(configurationServiceMock);
		EasyMock.verify(emailServiceMock);
	}

	public void testSetConfirmationCode() {
		UserEntity user = createTestUser();
		user.setConfirmed(true);
		impl.setConfirmationCode(user, "xxx");
		assertFalse(user.getConfirmed());
		assertEquals("xxx", user.getConfirmationCode());
		assertNotNull(user.getConfirmationRequestedAt());
	}

	public void testSendEmailNotificationToAdmins() {
		UserEntity user = createTestUser();
		List<UserEntity> users = new ArrayList<UserEntity>();
		users.add(user);
		EmailPlaceholderBean placeholder = impl.generateEmailPlaceHolder(user);
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
		UserEntity user = createTestUser();
		EasyMock.expect(userDaoMock.findUserByUsername("testuser")).andReturn(user);
		userDaoMock.save(user);
		EasyMock.replay(userDaoMock);
		impl.setNewPassword("testuser", "12345");
		assertNull(user.getForgotPasswordCode());
		assertEquals(PortalUtil.generateMd5("12345"), user.getPasswordMD5());
		EasyMock.verify(userDaoMock);
	}

	private UserEntity createTestUser() {
		UserEntity user = new UserEntity();
		user.setConfirmed(false);
		user.setUsername("testuser");
		user.setFirstname("max");
		user.setLastname("power");
		user.setEmail("max.power@no.domain");
		user.setPasswordMD5("xxx");
		user.setForgotPasswordCode("xxx");
		return user;
	}
}
