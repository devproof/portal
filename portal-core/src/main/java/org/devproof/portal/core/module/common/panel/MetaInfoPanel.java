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
package org.devproof.portal.core.module.common.panel;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.panel.UsernamePanel;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * the part in blogs downloads, etc "created by [name] at [date]
 * 
 * @author Carsten Hufe
 */
public class MetaInfoPanel extends Panel {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "displayDateFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	@SpringBean(name = "userService")
	private UserService userService;

	private BaseEntity entity;
	private String createdByName;
	private boolean existsCreatedByUser;
	private String modifiedByName;
	private boolean existsModifiedByUser;
	private boolean showModifiedBy;
	private boolean showRealAuthor;
	private boolean equalCreationModificationTime;
	private boolean sameAuthor;

	public MetaInfoPanel(String id, BaseEntity entity) {
		super(id);
		this.entity = entity;
		setShowRealAuthorName();
		setCreatedByUser();
		setModifiedByUser();
		setShowModifiedBy();
		setEqualCreationModificationTime();
		setSameAuthor();
		add(createCreatedContainer());
		add(createModifiedContainer());
		add(createSameModifierCreatorContainer());
	}

	private WebMarkupContainer createSameModifierCreatorContainer() {
		WebMarkupContainer sameModified = new WebMarkupContainer("sameModified");
		sameModified.add(createModifiedAtLabel());
		sameModified.setVisible(showModifiedBy && sameAuthor && !equalCreationModificationTime);
		return sameModified;
	}

	private void setSameAuthor() {
		sameAuthor = entity.getCreatedBy().equals(entity.getModifiedBy());
	}

	private void setEqualCreationModificationTime() {
		equalCreationModificationTime = entity.getCreatedAt().equals(entity.getModifiedAt());
	}

	private void setShowModifiedBy() {
		showModifiedBy = configurationService.findAsBoolean(CommonConstants.CONF_SHOW_MODIFIED_BY);
	}

	private Label createModifiedAtLabel() {
		return new Label("modifiedAt", dateFormat.format(entity.getModifiedAt()));
	}

	private WebMarkupContainer createModifiedContainer() {
		WebMarkupContainer modified = new WebMarkupContainer("modified");
		modified.add(createModifiedAtLabel());
		modified.add(createModifiedUsernamePanel());
		modified.setVisible(showModifiedBy && !sameAuthor && !equalCreationModificationTime);
		return modified;
	}

	private UsernamePanel createModifiedUsernamePanel() {
		return new UsernamePanel("modifiedBy", entity.getModifiedBy(), modifiedByName, existsModifiedByUser);
	}

	private WebMarkupContainer createCreatedContainer() {
		WebMarkupContainer created = new WebMarkupContainer("created");
		created.add(createCreatedAtLabel());
		created.add(createCreatedUsernamePanel());
		return created;
	}

	private UsernamePanel createCreatedUsernamePanel() {
		return new UsernamePanel("createdBy", entity.getCreatedBy(), createdByName, existsCreatedByUser);
	}

	private Label createCreatedAtLabel() {
		return new Label("createdAt", dateFormat.format(entity.getCreatedAt()));
	}

	private void setModifiedByUser() {
		modifiedByName = entity.getModifiedBy();
		UserEntity user = userService.findUserByUsername(modifiedByName);
		existsModifiedByUser = user != null;
		if (showModifiedBy && showRealAuthor) {
			if (user != null && StringUtils.isNotBlank(user.getFirstname())
					&& StringUtils.isNotBlank(user.getLastname())) {
				modifiedByName = user.getFirstname() + " " + user.getLastname();
			}
		}
	}

	private void setShowRealAuthorName() {
		showRealAuthor = configurationService.findAsBoolean(CommonConstants.CONF_SHOW_REAL_AUTHOR);
	}

	private void setCreatedByUser() {
		createdByName = entity.getCreatedBy();
		UserEntity user = userService.findUserByUsername(createdByName);
		existsCreatedByUser = user != null;
		if (showRealAuthor) {
			if (user != null && StringUtils.isNotBlank(user.getFirstname())
					&& StringUtils.isNotBlank(user.getLastname())) {
				createdByName = user.getFirstname() + " " + user.getLastname();
			}
		}
	}
}
