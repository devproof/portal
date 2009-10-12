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

import org.apache.wicket.Component;
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

	private PageParameters params;
	private String contentId;
	private OtherPageEntity otherPage;
	
	public OtherPageViewPage(final PageParameters params) {
		super(params);
		this.params = params;
		contentId = getContentIdParameter();
		otherPage = otherPageService.findOtherPageByContentId(contentId);

		add(createAuthorContainer());
		add(createAppropriateContentLabel());
		
		redirectToErrorPageIfHasNoRights();
	}

	private WebMarkupContainer createAuthorContainer() {
		WebMarkupContainer authorContainer = new WebMarkupContainer("authorContainer");
		authorContainer.setVisible(isAuthor());
		authorContainer.add(createAppropriateAuthorPanel());
		authorContainer.add(createAppropriateMetaInfoPanel());
		return authorContainer;
	}

	private Component createAppropriateContentLabel() {
		Component contentLabel;
		if (otherPage == null) {
			contentLabel = createNoContentLabel();
		} else {
			contentLabel = createContentLabel();
		}
		return contentLabel;
	}

	private Component createAppropriateMetaInfoPanel() {
		Component metaInfoPanel;
		if (otherPage == null) {
			metaInfoPanel = createHiddenMetaInfoPanel();
		} else {
			metaInfoPanel = createMetaInfoPanel();
		}
		return metaInfoPanel;
	}

	private ExtendedLabel createContentLabel() {
		return new ExtendedLabel("content", otherPage.getContent());
	}

	private MetaInfoPanel createMetaInfoPanel() {
		MetaInfoPanel metaInfo = new MetaInfoPanel("metaInfo", otherPage);
		return metaInfo;
	}

	private void redirectToErrorPageIfHasNoRights() {
		if (otherPage != null && hasRightToViewOtherPage(otherPage)) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(
					getString("missing.right"), getRequestURL()));
		}
	}

	private WebMarkupContainer createAppropriateAuthorPanel() {
		WebMarkupContainer authorPanel;
		if (isAuthor()) {
			authorPanel = createAuthorPanel();
		} else {
			authorPanel = createHiddenAuthorPanel();
		}
		return authorPanel;
	}

	private WebMarkupContainer createHiddenAuthorPanel() {
		WebMarkupContainer authorPanel = new WebMarkupContainer("authorButtons");
		authorPanel.setVisible(false);
		return authorPanel;
	}

	private AuthorPanel<OtherPageEntity> createAuthorPanel() {
		AuthorPanel<OtherPageEntity> authorPanel = newAuthorPanel();
		authorPanel.setRedirectPage(OtherPagePage.class, new PageParameters("infoMsg=" + getString("msg.deleted")));
		return authorPanel;
	}

	private AuthorPanel<OtherPageEntity> newAuthorPanel() {
		AuthorPanel<OtherPageEntity> authorButtons = new AuthorPanel<OtherPageEntity>("authorButtons", otherPage) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDelete(final AjaxRequestTarget target) {
				otherPageService.delete(getEntity());
			}

			@Override
			public void onEdit(final AjaxRequestTarget target) {
				OtherPageEntity editPage = otherPage;
				// create new empty page if not exists
				if (editPage == null) {
					editPage = otherPageService.newOtherPageEntity();
					editPage.setContentId(contentId);
				}
				setResponsePage(new OtherPageEditPage(editPage));
			}
		};
		return authorButtons;
	}

	private boolean hasRightToViewOtherPage(OtherPageEntity page) {
		PortalSession session = (PortalSession) getSession();
		return !session.hasRight("otherPage.view") && !session.hasRight(page.getViewRights());
	}

	private MetaInfoPanel createHiddenMetaInfoPanel() {
		MetaInfoPanel metaInfo = new MetaInfoPanel("metaInfo", createOtherPageEntity());
		metaInfo.setVisible(false);
		return metaInfo;
	}

	private ExtendedLabel createNoContentLabel() {
		return new ExtendedLabel("content", getString("noContent"));
	}

	private OtherPageEntity createOtherPageEntity() {
		OtherPageEntity tmp = otherPageService.newOtherPageEntity();
		tmp.setCreatedAt(PortalUtil.now());
		tmp.setModifiedAt(PortalUtil.now());
		tmp.setCreatedBy("");
		tmp.setModifiedBy("");
		return tmp;
	}

	private String getContentIdParameter() {
		String contentId = params.getString("0");
		if (contentId == null) {
			contentId = getRequest().getParameter("optparam");
		}
		return contentId;
	}
}
