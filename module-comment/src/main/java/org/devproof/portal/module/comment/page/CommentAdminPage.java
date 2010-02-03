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
package org.devproof.portal.module.comment.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.config.CommentConfiguration;
import org.devproof.portal.module.comment.panel.CommentPanel;
import org.devproof.portal.module.comment.panel.CommentSearchBoxPanel;
import org.devproof.portal.module.comment.query.CommentQuery;

/**
 * @author Carsten Hufe
 */
public class CommentAdminPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	public CommentAdminPage(PageParameters params) {
		super(params);
		CommentQuery query = new CommentQuery();
		addFilterBox(new CommentSearchBoxPanel("box", query) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {

			}
		});
		CommentConfiguration configuration = new CommentConfiguration();
		add(new CommentPanel("comments", configuration) {
			private static final long serialVersionUID = 1L;

			@Override
			public int getNumberOfPages() {
				return configurationService.findAsInteger(CommentConstants.CONF_COMMENT_NUMBER_PER_PAGE_ADMIN);
			}

			@Override
			public boolean hideInput() {
				return true;
			}
		});
	}
}
