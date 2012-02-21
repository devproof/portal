/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.app;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.protocol.http.ClientProperties;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.right.RightConstants;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.entity.User;
import org.devproof.portal.core.module.user.exception.AuthentificationFailedException;
import org.devproof.portal.core.module.user.exception.UserNotConfirmedException;
import org.devproof.portal.core.module.user.service.UserService;
import org.springframework.core.annotation.AnnotationUtils;

import javax.servlet.http.Cookie;
import java.util.Collection;
import java.util.List;

/**
 * Global portal session
 *
 * @author Carsten Hufe
 */
public class PortalSession extends WebSession {

    private static final long serialVersionUID = 1L;
    // private static final Log LOG = LogFactory.getLog(PortalSession.class);
    private static final int COOKIE_MAX_AGE = 3600 * 24 * 7; // 7 Days
    @SpringBean(name = "userService")
    protected UserService userService;
    @SpringBean(name = "roleService")
    protected RoleService roleService;
    @SpringBean(name = "rightService")
    protected RightService rightService;
    protected long dirtyTime = 0l;
    protected User user;

    public PortalSession(Request request) {
        super(request);
        injectSpringBeans();
    }

    protected void injectSpringBeans() {
        Injector.get().inject(this);
    }

    /**
     * Authentificates a user
     *
     * @param username Username
     * @param password Password
     * @return null if there is no error, if there is an error it returns the
     *         error message key
     */
    public final String authenticateUser(String username, String password) throws UserNotConfirmedException {
        try {
            user = userService.authentificate(username, password, getIpAddress());
            storeCookie();
            return null;
        } catch (AuthentificationFailedException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean authenticate(String username, String password) {
        try {
            String message = authenticateUser(username, password);
            return message == null;
        } catch (UserNotConfirmedException e) {
            return false;
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
            // LOG.debug("Store cookie.");
        }
    }

    /**
     * Get the user object
     *
     * @return logged in user
     */
    public User getUser() {
        if (user == null) {
            String sessionId = getSessionIdFromCookie();
            if (sessionId != null) {
                user = userService.authentificate(sessionId, getIpAddress());
                storeCookie();
            }
            if (user == null) {
                user = userService.findGuestUser();
            }
        }
        refreshRoleIfUpdated();
        return user;
    }

    protected String getSessionIdFromCookie() {
        Cookie cookie = getCookie();
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    protected Cookie getCookie() {
        WebRequest request = (WebRequest) RequestCycle.get().getRequest();
        return request.getCookie(CommonConstants.SESSION_ID_COOKIE);
    }

    private void refreshRoleIfUpdated() {
        long appDirtyTime = rightService.getDirtyTime();
        if (appDirtyTime != dirtyTime) {
            dirtyTime = appDirtyTime;
            Role role = roleService.findById(user.getRole().getId());
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
        // LOG.debug("Logout user " + user.getUsername());
        clearCookie();
        user = null;
    }

    protected void clearCookie() {
        Cookie cookie = getCookie();
        if (cookie != null) {
            cookie.setPath("/");
            ((WebResponse) RequestCycle.get().getResponse()).clearCookie(cookie);
        }
    }

    /**
     * Returns the role object
     *
     * @return the role of the user
     */
    public Role getRole() {
        return getUser().getRole();
    }

    /**
     * Returns the user rights
     *
     * @return user rights
     */
    public List<Right> getRights() {
        return getRole().getRights();
    }

    /**
     * Has this right
     *
     * @param rightName right as string
     * @return true if he has the right
     */
    public boolean hasRight(String rightName) {
        Right right = rightService.newRightEntity(rightName);
        return this.hasRight(right);
    }

    /**
     * Whether an user has this right
     *
     * @param right right entity
     * @return true if he has
     */
    public boolean hasRight(Right right) {
        User user = getUser();
        return user.getRole().getRights().contains(right);
    }

    /**
     * Whether an user has the right for this component
     * @param pageClazz page class
     * @param action action render or enable
     * @return true if the user has the right
     */
    public boolean hasRight(Class<? extends Page> pageClazz, Action action) {
        if (Page.class.isAssignableFrom(pageClazz)) {
            // false means the whole page is blocked
            if(pageClazz.isAnnotationPresent(Secured.class)) {
                return evaluateSecuredAnnotation(pageClazz, action);
            }
            else {
                List<Right> allRights = rightService.getAllRights();
                String rightName = RightConstants.PAGE_RIGHT_PREFIX + pageClazz.getSimpleName();
                Right right = rightService.newRightEntity(rightName);
                if (allRights.contains(right)) {
                    return hasRight(right);
                }
            }
        }
        return true;
    }

    /**
     * Whether an user has the right for this component
     * @param pageClazz page class
     * @return true if the user has the right
     */
    public boolean hasRight(Class<? extends Page> pageClazz) {
        return hasRight(pageClazz, null);
    }

    /**
     * Whether an user has the right for this component
     *
     * @param component component
     * @param action
     * @return true if the user has the right
     */
    public boolean hasRight(Component component, Action action) {
        List<Right> allRights = rightService.getAllRights();
        if (component instanceof BookmarkablePageLink<?>) {
            /*
                * This will remove the links for pages where the user hasn't got
                * rights. If there exists a right starting with page.PageClassName,
                * the user must have the right to access the page!
                */
            BookmarkablePageLink<?> l = (BookmarkablePageLink<?>) component;
            Class<? extends Page> pageClass = l.getPageClass();
            return hasRight(pageClass, action);
        }
        else if(hasSecuredAnnotation(component.getClass())) {
            return evaluateSecuredAnnotation(component.getClass(),action);
        }
        // problem with tree table, i dont know why
        else if (!(component instanceof TreeTable)) {
            String rightName = RightConstants.COMPONENT_RIGHT_PREFIX + component.getPage().getClass().getSimpleName() + "." + component.getId();
            Right right = rightService.newRightEntity(rightName);
            if (allRights.contains(right)) {
                return hasRight(right);
            }

            rightName = RightConstants.GENERAL_RIGHT_PREFIX + component.getClass().getSimpleName();
            right = rightService.newRightEntity(rightName);
            if (allRights.contains(right)) {
                return hasRight(right);
            }
        }
        return true;
    }

    private boolean evaluateSecuredAnnotation(Class<?> clazz, Action action) {
        // if the user do not have the right when page is annotated with @Secured, he is not allowed to visit
        // page with this annotation is always protected
        Secured secured = getSecuredAnnotation(clazz);
        if(action != null && !secured.action().equals(action.getName())) {
            return true;
        }
        for(String right : secured.value()) {
            if(hasRight(right)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSecuredAnnotation(Class<?> clazz) {
        Secured secured = AnnotationUtils.findAnnotation(clazz, Secured.class);
        return secured != null;
    }

    private Secured getSecuredAnnotation(Class<?> clazz) {
        return AnnotationUtils.findAnnotation(clazz, Secured.class);
    }

    /**
     * Returns true if he has one of the rights
     *
     * @param rights collection with rights
     * @return true if he has one right
     */
    public boolean hasRight(Collection<Right> rights) {
        User user = getUser();
        Collection<Right> userRights = user.getRole().getRights();
        for (Right right : rights) {
            if (userRights.contains(right)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if he has the admin right or one of these rights
     *
     * @param adminRight admin right
     * @param rights     collection with rights
     * @return true if he has one right
     */
    public boolean hasRight(String adminRight, Collection<Right> rights) {
        if (hasRight(adminRight)) {
            return true;
		}
		return hasRight(rights);
	}

	public static PortalSession get() {
		return (PortalSession) Session.get();
	}

}
