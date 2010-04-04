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
package org.devproof.portal.module.article.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.print.page.PrintPage;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.panel.ArticlePrintPanel;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * @author Carsten Hufe
 */
public class ArticlePrintPage extends PrintPage {
	@SpringBean(name = "articleService")
	private ArticleService articleService;

	public ArticlePrintPage(PageParameters params) {
		super(params);
	}

	@Override
	protected Component createPrintableComponent(String id, PageParameters params) {
		String contentId = getContentId(params);
		ArticleEntity article = articleService.findByContentId(contentId);
		validateAccessRights(article);
		return new ArticlePrintPanel(id, Model.of(article));
	}

	private String getContentId(PageParameters params) {
		String contentId = params.getString("0");
		if (contentId == null) {
			throw new RestartResponseAtInterceptPageException(MessagePage
					.getMessagePage(getString("missing.parameter")));
		}
		return contentId;
	}

	private void validateAccessRights(ArticleEntity article) {
		if (article == null || !isAllowedToRead(article)) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right")));
		}
	}

	private boolean isAllowedToRead(ArticleEntity article) {
		PortalSession session = (PortalSession) getSession();
		return session.hasRight(article.getReadRights()) || session.hasRight("article.read");
	}
}
