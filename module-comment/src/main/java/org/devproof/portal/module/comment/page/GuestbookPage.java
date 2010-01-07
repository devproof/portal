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
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.module.comment.config.CommentConfiguration;
import org.devproof.portal.module.comment.panel.CommentPanel;

/**
 * @author Carsten Hufe
 */
public class GuestbookPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	// @SpringBean(name = "configurationService")
	// private ConfigurationService configurationService;

	public GuestbookPage(PageParameters params) {
		super(params);
		CommentConfiguration config = new CommentConfiguration();
		config.setModuleName("guestbook");
		config.setModuleContentId("guestbook");
		config.setReadRight("guestbook.comment.read");
		config.setWriteRight("guestbook.comment.write");
		add(new CommentPanel("comments",config));
	}
}
