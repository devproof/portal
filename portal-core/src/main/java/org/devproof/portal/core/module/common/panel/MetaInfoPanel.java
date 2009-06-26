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
	@SpringBean(name = "dateFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private transient ConfigurationService configurationService;
	@SpringBean(name = "userService")
	private transient UserService userService;

	public MetaInfoPanel(final String id, final BaseEntity entity) {
		super(id);
		boolean showRealAuthor = this.configurationService.findAsBoolean(CommonConstants.CONF_SHOW_REAL_AUTHOR);
		boolean showModifiedBy = this.configurationService.findAsBoolean(CommonConstants.CONF_SHOW_MODIFIED_BY);
		String createdBy = entity.getCreatedBy();
		UserEntity user = this.userService.findUserByUsername(createdBy);
		if (showRealAuthor) {
			if (user != null && StringUtils.isNotBlank(user.getFirstname()) && StringUtils.isNotBlank(user.getLastname())) {
				createdBy = user.getFirstname() + " " + user.getLastname();
			}
		}

		WebMarkupContainer created = new WebMarkupContainer("created");
		created.add(new Label("createdAt", this.dateFormat.format(entity.getCreatedAt())));
		created.add(new UsernamePanel("createdBy", entity.getCreatedBy(), createdBy, user != null));
		this.add(created);

		String modifiedBy = entity.getModifiedBy();
		user = this.userService.findUserByUsername(modifiedBy);
		if (showModifiedBy && showRealAuthor) {
			if (user != null && StringUtils.isNotBlank(user.getFirstname()) && StringUtils.isNotBlank(user.getLastname())) {
				modifiedBy = user.getFirstname() + " " + user.getLastname();
			}
		}
		WebMarkupContainer modified = new WebMarkupContainer("modified");
		modified.add(new Label("modifiedAt", this.dateFormat.format(entity.getModifiedAt())));
		modified.add(new UsernamePanel("modifiedBy", entity.getModifiedBy(), modifiedBy, user != null));
		this.add(modified);
		modified.setVisible(showModifiedBy && !entity.getCreatedBy().equals(entity.getModifiedBy()));
	}

}
