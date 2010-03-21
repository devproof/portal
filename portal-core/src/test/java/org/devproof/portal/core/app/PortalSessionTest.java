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
package org.devproof.portal.core.app;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.util.tester.WicketTester;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.service.UserService;
import org.easymock.EasyMock;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Carsten Hufe
 */
public class PortalSessionTest extends TestCase {

	private PortalSession portalSession;
	private boolean cookieStored;
	private boolean cookieCleaned;
	private String cookieSessionId;

	@Override
	protected void setUp() throws Exception {
		cookieStored = false;
		cookieCleaned = false;
		cookieSessionId = null;
		new WicketTester();
		MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
		ServletWebRequest servletWebRequest = new ServletWebRequest(mockHttpServletRequest);
		portalSession = new PortalSession(servletWebRequest) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void injectSpringBeans() {
				userService = EasyMock.createStrictMock(UserService.class);
				roleService = EasyMock.createStrictMock(RoleService.class);
				rightService = EasyMock.createStrictMock(RightService.class);
			}

			@Override
			public String getIpAddress() {
				return "123.123.123.123";
			}

			@Override
			public void storeCookie() {
				cookieStored = true;
			}

			@Override
			protected void clearCookie() {
				cookieCleaned = true;
			}

			@Override
			protected String getSessionIdFromCookie() {
				return cookieSessionId;
			}
		};
	}

	public void testAuthenticate_success() throws Exception {
		UserEntity user = createUserWithRights();
		EasyMock.expect(portalSession.userService.authentificate("peter", "secretpasswd", "123.123.123.123"))
				.andReturn(user);
		EasyMock.replay(portalSession.userService);
		assertNull(portalSession.authenticate("peter", "secretpasswd"));
		assertTrue(cookieStored);
		EasyMock.verify(portalSession.userService);
	}

	public void testAuthenticate_failed() throws Exception {
		EasyMock.expect(portalSession.userService.authentificate("peter", "secretpasswd", "123.123.123.123")).andThrow(
				new AuthentificationFailedException("wrong password"));
		EasyMock.replay(portalSession.userService);
		assertEquals("wrong password", portalSession.authenticate("peter", "secretpasswd"));
		assertFalse(cookieStored);
		EasyMock.verify(portalSession.userService);
	}

	public void testAuthenticate_userNotConfirmed() {
		try {
			EasyMock.expect(portalSession.userService.authentificate("peter", "secretpasswd", "123.123.123.123"))
					.andThrow(new UserNotConfirmedException());
			EasyMock.replay(portalSession.userService);
			assertEquals("wrong password", portalSession.authenticate("peter", "secretpasswd"));
			EasyMock.verify(portalSession.userService);
			fail("Expect UserNotConfirmedException");
		} catch (UserNotConfirmedException e) {
			assertFalse(cookieStored);
			assertTrue("Expect this exception", true);
		} catch (AuthentificationFailedException e) {
			fail("Not expected Exception.");
		}

	}

	public void testIsSignedIn_true() {
		portalSession.user = new UserEntity();
		portalSession.user.setGuestRole(false);
		assertTrue(portalSession.isSignedIn());
	}

	public void testIsSignedIn_false() {
		portalSession.user = new UserEntity();
		portalSession.user.setGuestRole(true);
		assertFalse(portalSession.isSignedIn());
	}

	public void testGetUser_loggedInUser() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		assertEquals(user, portalSession.getUser());
	}

	public void testGetUser_automaticRelogin() {
		cookieSessionId = "testSessionId";
		UserEntity user = createUserWithRights();
		EasyMock.expect(portalSession.userService.authentificate(cookieSessionId, "123.123.123.123")).andReturn(user);
		EasyMock.replay(portalSession.userService);
		assertEquals(user, portalSession.getUser());
		EasyMock.verify(portalSession.userService);
	}

	public void testGetUser_guest() {
		UserEntity guest = new UserEntity();
		guest.setId(1);
		guest.setGuestRole(true);
		guest.setUsername("guest");
		EasyMock.expect(portalSession.userService.findGuestUser()).andReturn(guest);
		EasyMock.replay(portalSession.userService);
		assertEquals(guest, portalSession.getUser());
		EasyMock.verify(portalSession.userService);
	}

	public void testLogoutUser() {
		portalSession.logoutUser();
		assertTrue(cookieCleaned);
	}

	public void testGetRole() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		assertEquals(user.getRole(), portalSession.getRole());
	}

	public void testGetRights() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		assertEquals(user.getRole().getRights(), portalSession.getRights());
	}

	public void testHasRightString_true() {
		EasyMock.expect(portalSession.rightService.newRightEntity("sample1")).andReturn(new RightEntity("sample1"));
		EasyMock.expect(portalSession.rightService.getDirtyTime()).andReturn(0l);
		EasyMock.replay(portalSession.rightService);
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		assertTrue(portalSession.hasRight("sample1"));
		EasyMock.verify(portalSession.rightService);
	}

	public void testHasRightString_false() {
		EasyMock.expect(portalSession.rightService.newRightEntity("notexisting")).andReturn(
				new RightEntity("notexisting"));
		EasyMock.expect(portalSession.rightService.getDirtyTime()).andReturn(0l);
		EasyMock.replay(portalSession.rightService);
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		assertFalse(portalSession.hasRight("notexisting"));
		EasyMock.verify(portalSession.rightService);
	}

	public void testHasRightRightEntity_true() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		RightEntity right = new RightEntity("sample1");
		assertTrue(portalSession.hasRight(right));
	}

	public void testHasRightRightEntity_false() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		RightEntity right = new RightEntity("notexisting");
		assertFalse(portalSession.hasRight(right));
	}

	public void testHasRightCollectionOfRightEntity_true() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		List<RightEntity> rights = new ArrayList<RightEntity>(user.getRole().getRights());
		rights.remove(1);
		assertTrue(portalSession.hasRight(rights));
	}

	public void testHasRightCollectionOfRightEntity_false() {
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		List<RightEntity> rights = new ArrayList<RightEntity>();
		rights.add(new RightEntity("notexisting"));
		assertFalse(portalSession.hasRight(rights));
	}

	public void testHasRightStringCollectionOfRightEntity_matchingadminright() {
		EasyMock.expect(portalSession.rightService.newRightEntity("adminright")).andReturn(
				new RightEntity("adminright"));
		EasyMock.expect(portalSession.rightService.getDirtyTime()).andReturn(0l);
		EasyMock.replay(portalSession.rightService);
		UserEntity user = createUserWithRights();
		user.getRole().add(new RightEntity("adminright"));
		portalSession.user = user;
		assertTrue(portalSession.hasRight("adminright", new ArrayList<RightEntity>()));
		EasyMock.verify(portalSession.rightService);
	}

	public void testHasRightStringCollectionOfRightEntity_noadminright() {
		EasyMock.expect(portalSession.rightService.newRightEntity("adminright")).andReturn(
				new RightEntity("adminright"));
		EasyMock.expect(portalSession.rightService.getDirtyTime()).andReturn(0l).times(2);
		EasyMock.replay(portalSession.rightService);
		UserEntity user = createUserWithRights();
		portalSession.user = user;
		assertFalse(portalSession.hasRight("adminright", new ArrayList<RightEntity>()));
		EasyMock.verify(portalSession.rightService);
	}

	private UserEntity createUserWithRights() {
		RoleEntity role = new RoleEntity();
		role.setId(1);
		role.setDescription("roleName");
		role.getRights().add(new RightEntity("sample1"));
		role.getRights().add(new RightEntity("sample2"));
		UserEntity user = new UserEntity();
		user.setId(1);
		user.setUsername("auser");
		user.setRole(role);
		return user;
	}
}
