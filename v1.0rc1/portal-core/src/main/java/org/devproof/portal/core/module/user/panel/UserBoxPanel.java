/*
 * Copyright 2009 Carsten Hufe devproof.org
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

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.user.page.SettingsPage;

/**
 * User info box with settings and logout link
 * 
 * @author Carsten Hufe
 */
public class UserBoxPanel extends Panel {

	private static final long serialVersionUID = 1L;

	public UserBoxPanel(final String id) {
		super(id);
		final PortalSession session = (PortalSession) getSession();
		this.add(new Label("username", session.getUser().getUsername() + " - " + session.getUser().getRole().getDescription()));

		final StatelessLink logoutLink = new StatelessLink("logoutLink") {

			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				this.setResponsePage(MessagePage.getMessagePageWithLogout(this.getString("loggedout")));
			}

		};
		this.add(new BookmarkablePageLink<Void>("settingsLink", SettingsPage.class));
		this.add(logoutLink);
	}
}
