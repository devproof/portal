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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.contact.ContactConstants;
import org.devproof.portal.core.module.contact.page.ContactPage;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * Linking a username to its contact page
 * 
 * @author Carsten Hufe
 */
public class UsernamePanel extends Panel {
	private static final long serialVersionUID = 1L;
    @SpringBean(name = "userService")
	private UserService userService;
    private IModel<String> usernameModel;
    private IModel<UserEntity> userModel;

    public UsernamePanel(String id, IModel<String> usernameModel) {
		super(id);
        this.usernameModel = usernameModel;
        this.userModel = createUserModel();
		add(createContactPageLink());
	}

    private IModel<UserEntity> createUserModel() {
        return new LoadableDetachableModel<UserEntity>() {
            private static final long serialVersionUID = 5479671452769963088L;
            @Override
            protected UserEntity load() {
                return userService.findUserByUsername(usernameModel.getObject());
            }
        };
    }

    private WebMarkupContainer createContactPageLink() {
		BookmarkablePageLink<ContactPage> link = newContactPageLink();
		link.add(createUsernameLabel());
        link.setParameter("0", usernameModel.getObject());
		return link;
	}

	private BookmarkablePageLink newContactPageLink() {
        return new BookmarkablePageLink<ContactPage>("userLink", ContactPage.class) {
            private static final long serialVersionUID = 8519679858021257340L;

            @Override
            public boolean isEnabled() {
                PortalSession session = (PortalSession) getSession();
                UserEntity user = userModel.getObject();
                boolean exists = user != null;
                return session.hasRight(ContactConstants.CONTACT_RIGHT) && exists && contactFormEnabled();
            }
        };
	}

	private Label createUsernameLabel() {
        IModel<String> usernameDisplayModel = createUsernameDisplayModel();
        return new Label("username", usernameDisplayModel);
	}

    private IModel<String> createUsernameDisplayModel() {
        return new LoadableDetachableModel<String>() {
            private static final long serialVersionUID = -1304908710263470243L;
            @Override
            protected String load() {
                String username = usernameModel.getObject();
                UserEntity user = userModel.getObject(); 
                if (showRealName()) {
                    if (user != null && StringUtils.isNotBlank(user.getFirstname())
                            && StringUtils.isNotBlank(user.getLastname())) {
                        username = user.getFirstname() + " " + user.getLastname();
                    }
                }
                return username;
            }
        };
	}

    /**
     * Hook method
     */
    protected boolean showRealName() {
        return true;
    }

    protected boolean contactFormEnabled() {
        return true;
    }
}
