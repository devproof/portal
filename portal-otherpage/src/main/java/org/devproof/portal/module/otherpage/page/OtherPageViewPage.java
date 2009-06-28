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
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.devproof.portal.module.otherpage.service.OtherPageService;

/**
 * @author Carsten Hufe
 */
public class OtherPageViewPage extends OtherPageBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "otherPageService")
	private OtherPageService otherPageService;

	public OtherPageViewPage(final PageParameters params) {
		super(params);
		PortalSession session = (PortalSession) getSession();
		String contentId = params.getString("0");
		if (contentId == null) {
			contentId = getRequest().getParameter("optparam");
		}
		final OtherPageEntity page = this.otherPageService.findOtherPageByContentId(contentId);

		WebMarkupContainer authorPanel = new WebMarkupContainer("authorPanel");
		authorPanel.setVisible(isAuthor());
		this.add(authorPanel);
		if (isAuthor()) {
			final String internalContentId = contentId;
			authorPanel.add(new AuthorPanel<OtherPageEntity>("authorButtons", page) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					OtherPageViewPage.this.otherPageService.delete(getEntity());
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					OtherPageEntity editPage = page;
					// create new empty page if not exists
					if (editPage == null) {
						editPage = OtherPageViewPage.this.otherPageService.newOtherPageEntity();
						editPage.setContentId(internalContentId);
					}
					this.setResponsePage(new OtherPageEditPage(editPage));
				}
			}.setRedirectPage(OtherPagePage.class, new PageParameters("infoMsg=" + this.getString("msg.deleted"))));
		} else {
			authorPanel.add(new WebMarkupContainer("authorButtons").setVisible(false));
		}

		if (page == null) {
			OtherPageEntity tmp = this.otherPageService.newOtherPageEntity();
			tmp.setCreatedAt(PortalUtil.now());
			tmp.setModifiedAt(PortalUtil.now());
			tmp.setCreatedBy("");
			tmp.setModifiedBy("");
			authorPanel.add(new MetaInfoPanel("metaInfo", tmp).setVisible(false));
			this.add(new ExtendedLabel("content", this.getString("noContent")));
		} else {
			authorPanel.add(new MetaInfoPanel("metaInfo", page));
			this.add(new ExtendedLabel("content", page.getContent()));
			if (!session.hasRight("otherPage.view") && !session.hasRight(page.getViewRights())) {
				throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right"), getRequestURL()));
			}
		}
	}
}
