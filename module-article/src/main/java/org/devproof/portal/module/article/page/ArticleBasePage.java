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

import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * @author Carsten Hufe
 */
public class ArticleBasePage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "articleService")
	private ArticleService articleService;

	private boolean isAuthor = false;

	public ArticleBasePage(PageParameters params) {
		super(params);
		add(createCSSHeaderContributor());
		addSyntaxHighlighter();
		addArticleAddLink();
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(ArticleConstants.REF_ARTICLE_CSS);
	}

	private void addArticleAddLink() {
		if (isAuthor()) {
            Link<?> addLink = createArticleAddLink();
			addPageAdminBoxLink(addLink);
		}
	}

    private Link<?> createArticleAddLink() {
        Link<?> addLink = newArticleAddLink();
        addLink.add(new Label(getPageAdminBoxLinkLabelId(), getString("createLink")));
        return addLink;
    }

    private Link<?> newArticleAddLink() {
		return new Link<Object>(getPageAdminBoxLinkId()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				ArticleEntity newEntry = articleService.newArticleEntity();
				setResponsePage(new ArticleEditPage(Model.of(newEntry)));
			}
		};
	}

	public boolean isAuthor() {
		PortalSession session = (PortalSession) getSession();
		return session.hasRight(ArticleConstants.AUTHOR_RIGHT);
	}
}
