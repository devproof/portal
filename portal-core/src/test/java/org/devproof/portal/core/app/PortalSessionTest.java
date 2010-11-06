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
package org.devproof.portal.core.app;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.service.UserService;
import org.devproof.portal.test.MockContextLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class,
        locations = {"classpath:/org/devproof/portal/core/test-datasource.xml" })
public class PortalSessionTest {

    private PortalSession portalSession;
    private boolean cookieStored;
    private boolean cookieCleaned;
    private String cookieSessionId;

    @Before
    public void setUp() throws Exception {
        cookieStored = false;
        cookieCleaned = false;
        cookieSessionId = null;
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(mockHttpServletRequest);
        portalSession = new PortalSession(servletWebRequest) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void injectSpringBeans() {
                userService = createStrictMock(UserService.class);
                roleService = createStrictMock(RoleService.class);
                rightService = createStrictMock(RightService.class);
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

    @Test
    public void testAuthenticate_success() throws Exception {
        User user = createUserWithRights();
        expect(portalSession.userService.authentificate("peter", "secretpasswd", "123.123.123.123")).andReturn(user);
        replay(portalSession.userService);
        assertNull(portalSession.authenticate("peter", "secretpasswd"));
        assertTrue(cookieStored);
        verify(portalSession.userService);
    }

    @Test
    public void testAuthenticate_failed() throws Exception {
        //noinspection ThrowableInstanceNeverThrown
        expect(portalSession.userService.authentificate("peter", "secretpasswd", "123.123.123.123")).andThrow(new AuthentificationFailedException("wrong password"));
        replay(portalSession.userService);
        assertEquals("wrong password", portalSession.authenticate("peter", "secretpasswd"));
        assertFalse(cookieStored);
        verify(portalSession.userService);
    }

    @Test
    public void testAuthenticate_userNotConfirmed() {
        try {
            //noinspection ThrowableInstanceNeverThrown
            expect(portalSession.userService.authentificate("peter", "secretpasswd", "123.123.123.123")).andThrow(new UserNotConfirmedException());
            replay(portalSession.userService);
            assertEquals("wrong password", portalSession.authenticate("peter", "secretpasswd"));
            verify(portalSession.userService);
            fail("Expect UserNotConfirmedException");
        } catch (UserNotConfirmedException e) {
            assertFalse(cookieStored);
            assertTrue("Expect this exception", true);
        } catch (AuthentificationFailedException e) {
            fail("Not expected Exception.");
        }

    }

    @Test
    public void testIsSignedIn_true() {
        portalSession.user = new User();
        portalSession.user.setGuestRole(false);
        assertTrue(portalSession.isSignedIn());
    }

    @Test
    public void testIsSignedIn_false() {
        portalSession.user = new User();
        portalSession.user.setGuestRole(true);
        assertFalse(portalSession.isSignedIn());
    }

    @Test
    public void testGetUser_loggedInUser() {
        User user = createUserWithRights();
        portalSession.user = user;
        assertEquals(user, portalSession.getUser());
    }

    @Test
    public void testGetUser_automaticRelogin() {
        cookieSessionId = "testSessionId";
        User user = createUserWithRights();
        expect(portalSession.userService.authentificate(cookieSessionId, "123.123.123.123")).andReturn(user);
        replay(portalSession.userService);
        assertEquals(user, portalSession.getUser());
        verify(portalSession.userService);
    }

    @Test
    public void testGetUser_guest() {
        User guest = new User();
        guest.setId(1);
        guest.setGuestRole(true);
        guest.setUsername("guest");
        expect(portalSession.userService.findGuestUser()).andReturn(guest);
        replay(portalSession.userService);
        assertEquals(guest, portalSession.getUser());
        verify(portalSession.userService);
    }

    @Test
    public void testLogoutUser() {
        portalSession.logoutUser();
        assertTrue(cookieCleaned);
    }

    @Test
    public void testGetRole() {
        User user = createUserWithRights();
        portalSession.user = user;
        assertEquals(user.getRole(), portalSession.getRole());
    }

    @Test
    public void testGetRights() {
        User user = createUserWithRights();
        portalSession.user = user;
        assertEquals(user.getRole().getRights(), portalSession.getRights());
    }

    @Test
    public void testHasRightString_true() {
        expect(portalSession.rightService.newRightEntity("sample1")).andReturn(new Right("sample1"));
        expect(portalSession.rightService.getDirtyTime()).andReturn(0l);
        replay(portalSession.rightService);
        portalSession.user = createUserWithRights();
        assertTrue(portalSession.hasRight("sample1"));
        verify(portalSession.rightService);
    }

    @Test
    public void testHasRightString_false() {
        expect(portalSession.rightService.newRightEntity("notexisting")).andReturn(new Right("notexisting"));
        expect(portalSession.rightService.getDirtyTime()).andReturn(0l);
        replay(portalSession.rightService);
        portalSession.user = createUserWithRights();
        assertFalse(portalSession.hasRight("notexisting"));
        verify(portalSession.rightService);
    }

    @Test
    public void testHasRightRightEntity_true() {
        portalSession.user = createUserWithRights();
        Right right = new Right("sample1");
        assertTrue(portalSession.hasRight(right));
    }

    @Test
    public void testHasRightRightEntity_false() {
        portalSession.user = createUserWithRights();
        Right right = new Right("notexisting");
        assertFalse(portalSession.hasRight(right));
    }

    @Test
    public void testHasRightCollectionOfRightEntity_true() {
        User user = createUserWithRights();
        portalSession.user = user;
        List<Right> rights = new ArrayList<Right>(user.getRole().getRights());
        rights.remove(1);
        assertTrue(portalSession.hasRight(rights));
    }

    @Test
    public void testHasRightCollectionOfRightEntity_false() {
        portalSession.user = createUserWithRights();
        List<Right> rights = new ArrayList<Right>();
        rights.add(new Right("notexisting"));
        assertFalse(portalSession.hasRight(rights));
    }

    @Test
    public void testHasRightStringCollectionOfRightEntity_matchingadminright() {
        expect(portalSession.rightService.newRightEntity("adminright")).andReturn(new Right("adminright"));
        expect(portalSession.rightService.getDirtyTime()).andReturn(0l);
        replay(portalSession.rightService);
        User user = createUserWithRights();
        user.getRole().add(new Right("adminright"));
        portalSession.user = user;
        assertTrue(portalSession.hasRight("adminright", new ArrayList<Right>()));
        verify(portalSession.rightService);
    }

    @Test
    public void testHasRightStringCollectionOfRightEntity_noadminright() {
        expect(portalSession.rightService.newRightEntity("adminright")).andReturn(new Right("adminright"));
        expect(portalSession.rightService.getDirtyTime()).andReturn(0l).times(2);
        replay(portalSession.rightService);
        portalSession.user = createUserWithRights();
        assertFalse(portalSession.hasRight("adminright", new ArrayList<Right>()));
        verify(portalSession.rightService);
    }

    private User createUserWithRights() {
        Role role = new Role();
        role.setId(1);
        role.setDescription("roleName");
        role.getRights().add(new Right("sample1"));
        role.getRights().add(new Right("sample2"));
        User user = new User();
        user.setId(1);
        user.setUsername("auser");
        user.setRole(role);
        return user;
    }
}
