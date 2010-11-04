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
package org.devproof.portal.module.comment.panel;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.user.panel.UsernamePanel;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.entity.Comment;

/**
 * @author Carsten Hufe
 */
public class CommentInfoPanel extends Panel {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "displayDateTimeFormat")
	private SimpleDateFormat dateFormat;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private IModel<Comment> commentModel;

	public CommentInfoPanel(String id, IModel<Comment> commentModel) {
		super(id);
		this.commentModel = commentModel;
		add(createCreatedContainer());
	}

	private WebMarkupContainer createCreatedContainer() {
		WebMarkupContainer created = new WebMarkupContainer("created");
		created.add(createCreatedAtLabel());
		created.add(createCreatedUsernamePanel());
		return created;
	}

	private UsernamePanel createCreatedUsernamePanel() {
		return new UsernamePanel("createdBy", createCreatedByUserModel()) {
			private static final long serialVersionUID = -6323896736697531921L;

			@Override
			protected boolean showRealName() {
				return showRealAuthorName();
			}

			@Override
			protected boolean contactFormEnabled() {
				Comment comment = commentModel.getObject();
				return StringUtils.isBlank(comment.getGuestName());
			}
		};
	}

	private Label createCreatedAtLabel() {
		AbstractReadOnlyModel<Object> createdAtModel = createCreatedAtModel();
		return new Label("createdAt", createdAtModel);
	}

	private AbstractReadOnlyModel<Object> createCreatedAtModel() {
		return new AbstractReadOnlyModel<Object>() {
			private static final long serialVersionUID = 3325298161009039240L;

			@Override
			public Object getObject() {
				Comment comment = commentModel.getObject();
				return dateFormat.format(comment.getCreatedAt());
			}
		};
	}

	private boolean showRealAuthorName() {
		return configurationService.findAsBoolean(CommentConstants.CONF_SHOW_REAL_AUTHOR);
	}

	private IModel<String> createCreatedByUserModel() {
		return new AbstractReadOnlyModel<String>() {
			private static final long serialVersionUID = 2143000810085055343L;

			@Override
			public String getObject() {
				Comment comment = commentModel.getObject();
				if (StringUtils.isBlank(comment.getGuestName())) {
					return comment.getCreatedBy();
				} else {
					return getString("guestPrefix") + comment.getGuestName();
				}
			}
		};
	}
}
