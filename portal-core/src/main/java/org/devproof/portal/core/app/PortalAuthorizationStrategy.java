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

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.extensions.markup.html.tree.table.TreeTable;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.devproof.portal.core.module.right.RightConstants;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.user.panel.LoginBoxPanel;
import org.devproof.portal.core.module.user.panel.UserBoxPanel;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * The whole role and rights system of the portal
 * 
 * @author Carsten Hufe
 * 
 */
public class PortalAuthorizationStrategy implements IAuthorizationStrategy {
	private RightService rightService;

	public PortalAuthorizationStrategy(ApplicationContext context) {
		rightService = (RightService) context.getBean("rightService");
	}

	public boolean isActionAuthorized(Component component, Action action) {
		// false means the component will not be rendered
		PortalSession session = ((PortalSession) Session.get());
		List<RightEntity> allRights = rightService.getAllRights();
		if (component instanceof LoginBoxPanel) {
			return !session.isSignedIn();
		} else if (component instanceof UserBoxPanel) {
			return session.isSignedIn();
		} else if (component instanceof BookmarkablePageLink<?>) {
			/*
			 * This will remove the links for pages where the user hasn't got
			 * rights. If there exists a right starting with page.PageClassName,
			 * the user must have the right to access the page!
			 */
			BookmarkablePageLink<?> l = (BookmarkablePageLink<?>) component;
			Class<?> pageClazz = l.getPageClass();
			String rightName = RightConstants.PAGE_RIGHT_PREFIX + pageClazz.getSimpleName();
			RightEntity right = rightService.newRightEntity(rightName);
			if (allRights.contains(right)) {
				return session.hasRight(right);
			}
			return true;
		}
		// problem with tree table, i dont know why
		else if (!(component instanceof TreeTable)) {
			String rightName = RightConstants.COMPONENT_RIGHT_PREFIX + component.getPage().getClass().getSimpleName()
					+ "." + component.getId();
			RightEntity right = rightService.newRightEntity(rightName);
			if (allRights.contains(right)) {
				return session.hasRight(right);
			}

			rightName = RightConstants.GENERAL_RIGHT_PREFIX + component.getClass().getSimpleName();
			right = rightService.newRightEntity(rightName);
			if (allRights.contains(right)) {
				return session.hasRight(right);
			}
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean isInstantiationAuthorized(Class componentClass) {
		// false means the whole page is blocked
		PortalSession session = ((PortalSession) Session.get());
		List<RightEntity> allRights = rightService.getAllRights();
		if (Page.class.isAssignableFrom(componentClass)) {
			String rightName = RightConstants.PAGE_RIGHT_PREFIX + componentClass.getSimpleName();
			RightEntity right = rightService.newRightEntity(rightName);
			if (allRights.contains(right)) {
				return session.hasRight(right);
			}
		}
		return true;
	}
}
