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
package org.devproof.portal.core.module.user.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.StatelessLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.NavigationBox;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.user.page.SettingsPage;

/**
 * User info box with settings and logout link
 *
 * @author Carsten Hufe
 */
// todo nur bei eingeloggtem user rendern
@NavigationBox("User Box")
public class UserBoxPanel extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;
    private WebMarkupContainer titleContainer;

    public UserBoxPanel(String id) {
        super(id);
        add(createTitleContainer());
        add(createSettingsLink());
        add(createLogoutLink());
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        titleContainer.add(createTitleLabel());
        return titleContainer;
    }

    private Label createTitleLabel() {
        PortalSession session = (PortalSession) getSession();
        return new Label("username", session.getUser().getUsername() + " - " + session.getUser().getRole().getDescription());
    }

    private BookmarkablePageLink<Void> createSettingsLink() {
        return new BookmarkablePageLink<Void>("settingsLink", SettingsPage.class);
    }

    private StatelessLink createLogoutLink() {
        return new StatelessLink("logoutLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                setResponsePage(MessagePage.getMessagePageWithLogout(getString("loggedout")));
            }
        };
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }
}
