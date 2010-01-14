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
package org.devproof.portal.module.comment.panel;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.panel.UsernamePanel;
import org.devproof.portal.core.module.user.service.UserService;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.entity.CommentEntity;

/**
 * @author Carsten Hufe
 */
public class CommentInfoPanel extends Panel {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "displayDateTimeFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	@SpringBean(name = "userService")
	private UserService userService;

	private CommentEntity comment;

	private String createdByName;
	private boolean existsCreatedByUser = false;
	private boolean showRealAuthor;

	public CommentInfoPanel(String id, CommentEntity comment) {
		super(id);
		this.comment = comment;
		setShowRealAuthorName();
		setCreatedByUser();
		add(createCreatedContainer());
	}

	private WebMarkupContainer createCreatedContainer() {
		WebMarkupContainer created = new WebMarkupContainer("created");
		created.add(createCreatedAtLabel());
		created.add(createCreatedUsernamePanel());
		return created;
	}

	private UsernamePanel createCreatedUsernamePanel() {
		return new UsernamePanel("createdBy", comment.getCreatedBy(), createdByName, existsCreatedByUser);
	}

	private Label createCreatedAtLabel() {
		return new Label("createdAt", dateFormat.format(comment.getCreatedAt()));
	}

	private void setShowRealAuthorName() {
		showRealAuthor = configurationService.findAsBoolean(CommentConstants.CONF_SHOW_REAL_AUTHOR);
	}

	private void setCreatedByUser() {
		if (StringUtils.isBlank(comment.getGuestName())) {
			createdByName = comment.getCreatedBy();
			UserEntity user = userService.findUserByUsername(createdByName);
			existsCreatedByUser = user != null;
			if (showRealAuthor) {
				if (user != null && StringUtils.isNotBlank(user.getFirstname())
						&& StringUtils.isNotBlank(user.getLastname())) {
					createdByName = user.getFirstname() + " " + user.getLastname();
				}
			}
		} else {
			createdByName = getString("guestPrefix") + comment.getGuestName();
		}
	}
}
