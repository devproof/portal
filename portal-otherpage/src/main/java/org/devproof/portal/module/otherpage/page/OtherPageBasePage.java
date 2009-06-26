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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.devproof.portal.module.otherpage.service.OtherPageService;

/**
 * @author Carsten Hufe
 */
public class OtherPageBasePage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	private final boolean isAuthor;
	@SpringBean(name = "otherPageService")
	private transient OtherPageService otherPageService;

	public OtherPageBasePage(final PageParameters params) {
		super(params);
		PortalSession session = (PortalSession) getSession();
		this.isAuthor = session.hasRight("page.OtherPagePage");
		addSyntaxHighlighter();
		if (this.isAuthor) {
			addPageAdminBoxLink(new Link<OtherPageEntity>("adminLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					final OtherPageEntity newEntry = OtherPageBasePage.this.otherPageService.newOtherPageEntity();
					this.setResponsePage(new OtherPageEditPage(newEntry));
				}
			}.add(new Label("linkName", this.getString("createLink"))));
		}
	}

	public boolean isAuthor() {
		return this.isAuthor;
	}
}
