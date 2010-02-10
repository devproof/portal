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

import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Session;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * Global portal session
 * 
 * @author Carsten Hufe
 * 
 */
public class PortalSession extends WebSession {

	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(PortalSession.class);
	private static final int COOKIE_MAX_AGE = 3600 * 24 * 7; // 7 Days
	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "roleService")
	private RoleService roleService;
	@SpringBean(name = "rightService")
	private RightService rightService;
	private long dirtyTime = 0l;
	private UserEntity user;

	public PortalSession(Request request) {
		super(request);
		InjectorHolder.getInjector().inject(this);
	}

	/**
	 * Authentificates a user
	 * 
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 * @return null if there is no error, if there is an error it returns the
	 *         error message key
	 */
	public final String authenticate(String username, String password) throws UserNotConfirmedException {
		try {
			user = userService.authentificate(username, password, getIpAddress());
			getSessionStore().getSessionId(RequestCycle.get().getRequest(), true);
			// Bind because the Login form is stateless...
			storeCookie();
			return null;
		} catch (AuthentificationFailedException e) {
			return e.getMessage();
		}
	}

	/**
	 * User is logged in?
	 * 
	 * @return true if logged in
	 */
	public boolean isSignedIn() {
		return !getUser().isGuestRole();
	}

	/**
	 * Stores a cookie for the relogin
	 */
	public void storeCookie() {
		if (user != null && !user.isGuestRole()) {
			Cookie cookie = new Cookie(CommonConstants.SESSION_ID_COOKIE, user.getSessionId());
			cookie.setMaxAge(COOKIE_MAX_AGE);
			cookie.setPath("/");
			((WebResponse) RequestCycle.get().getResponse()).addCookie(cookie);
			LOG.debug("Store cookie.");
		}
	}

	/**
	 * Get the user object
	 * 
	 * @return logged in user
	 */
	public UserEntity getUser() {
		if (user == null) {
			WebRequest request = (WebRequest) RequestCycle.get().getRequest();
			Cookie cookie = request.getCookie(CommonConstants.SESSION_ID_COOKIE);
			if (cookie != null) {
				String sessionId = cookie.getValue();
				if (sessionId != null) {
					user = userService.authentificate(sessionId, getIpAddress());
					storeCookie();
				}
			}
			if (user == null) {
				user = userService.findGuestUser();
			}
		}
		refreshRoleIfUpdated();
		return user;
	}

	private void refreshRoleIfUpdated() {
		long appDirtyTime = rightService.getDirtyTime();
		if (appDirtyTime != dirtyTime) {
			dirtyTime = appDirtyTime;
			RoleEntity role = roleService.findById(user.getRole().getId());
			user.setRole(role);
		}
	}

	public String getIpAddress() {
		ClientProperties prop = ((WebClientInfo) getClientInfo()).getProperties();
		return prop.getRemoteAddress();
	}

	/**
	 * logs the user out
	 */
	public void logoutUser() {
		LOG.debug("Logout user " + user.getUsername());
		Cookie cookie = ((WebRequest) RequestCycle.get().getRequest()).getCookie(CommonConstants.SESSION_ID_COOKIE);
		if (cookie != null) {
			cookie.setPath("/");
			((WebResponse) RequestCycle.get().getResponse()).clearCookie(cookie);
		}
		user = null;
	}

	/**
	 * Returns the role object
	 * 
	 * @return the role of the user
	 */
	public RoleEntity getRole() {
		return getUser().getRole();
	}

	/**
	 * Returns the user rights
	 * 
	 * @return user rights
	 */
	public List<RightEntity> getRights() {
		return getRole().getRights();
	}

	/**
	 * Has this right
	 * 
	 * @param rightName
	 *            right as string
	 * @return true if he has the right
	 */
	public boolean hasRight(String rightName) {
		RightEntity right = rightService.newRightEntity(rightName);
		return this.hasRight(right);
	}

	/**
	 * Whether a user has this right
	 * 
	 * @param right
	 *            right entity
	 * @return true if he has
	 */
	public boolean hasRight(RightEntity right) {
		UserEntity user = getUser();
		return user.getRole().getRights().contains(right);
	}

	/**
	 * Returns true if he has one of the rights
	 * 
	 * @param rights
	 *            collection with rights
	 * @return true if he has one right
	 */
	public boolean hasRight(Collection<RightEntity> rights) {
		UserEntity user = getUser();
		Collection<RightEntity> userRights = user.getRole().getRights();
		for (RightEntity right : rights) {
			if (userRights.contains(right)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if he has the admin right or one of these rights
	 * 
	 * @param adminRight
	 *            admin right
	 * @param rights
	 *            collection with rights
	 * @return true if he has one right
	 */
	public boolean hasRight(String adminRight, Collection<RightEntity> rights) {
		if (this.hasRight(adminRight)) {
			return true;
		}
		return this.hasRight(rights);
	}

	public static PortalSession get() {
		return (PortalSession) Session.get();
	}
}
