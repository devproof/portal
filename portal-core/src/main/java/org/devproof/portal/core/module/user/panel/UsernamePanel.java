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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.contact.page.ContactPage;

/**
 * Linking a username to its contact page
 * 
 * @author Carsten Hufe
 */
public class UsernamePanel extends Panel {
	private static final long serialVersionUID = 1L;

	public UsernamePanel(String id, String username, String displayName, boolean exists) {
		super(id);
		PortalSession session = (PortalSession) getSession();
		WebMarkupContainer link = null;
		if (session.hasRight("page.ContactPage") && exists) {
			link = new BookmarkablePageLink<ContactPage>("userLink", ContactPage.class).setParameter("0", username);
		} else {
			link = new WebMarkupContainer("userLink");
		}
		link.add(new Label("username", displayName));
		add(link);
	}
}
