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
package org.devproof.portal.module.article.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * Article overview page
 * 
 * @author Carsten Hufe
 */
public class ArticleViewPage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "articleService")
	private transient ArticleService articleService;
	@SpringBean(name = "articleTagService")
	private transient TagService<ArticleTagEntity> articleTagService;

	public ArticleViewPage(final PageParameters params) {
		super(params);
		final PortalSession session = (PortalSession) getSession();
		this.addTagCloudBox(this.articleTagService, new PropertyModel<ArticleTagEntity>(new ArticleQuery(), "tag"), ArticlePage.class, params);
		String contentId = params.getString("0");
		if (contentId == null) {
			contentId = getRequest().getParameter("optparam");
		}
		final int currentPage = params.getAsInteger("1", 1);
		final int pageCount = (int) this.articleService.getPageCount(contentId);
		final ArticlePageEntity page = this.articleService.findArticlePageByContentIdAndPage(contentId, currentPage);

		if (page == null) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this.getString("error.page")));
		}
		if (page != null && !session.hasRight("article.read") && !session.hasRight(page.getArticle().getReadRights())) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(this.getString("missing.right"), getRequestURL()));
		}
		this.add(new Label("title", page.getArticle().getTitle()));
		setPageTitle(page.getArticle().getTitle());
		this.add(new MetaInfoPanel("metaInfo", page.getArticle()));
		if (isAuthor()) {
			this.add(new AuthorPanel<ArticleEntity>("authorButtons", page.getArticle()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					ArticleViewPage.this.articleService.delete(page.getArticle());
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					ArticleEntity article = page.getArticle();
					article = ArticleViewPage.this.articleService.findById(article.getId());
					final ArticleEditPage articlePage = new ArticleEditPage(article);
					this.setResponsePage(articlePage);
				}
			}.setRedirectPage(ArticlePage.class, new PageParameters("infoMsg=" + this.getString("msg.deleted"))));
		} else {
			this.add(new WebMarkupContainer("authorButtons").setVisible(false));
		}
		this.add(new ContentTagPanel<ArticleTagEntity>("tags", new ListModel<ArticleTagEntity>(page.getArticle().getTags()), ArticlePage.class, params));
		this.add(new ExtendedLabel("content", page.getContent()));

		final BookmarkablePageLink<String> backLink = new BookmarkablePageLink<String>("backLink", ArticleViewPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return currentPage > 1;
			}
		};
		backLink.setParameter("0", contentId);
		backLink.setParameter("1", currentPage - 1);
		this.add(backLink);

		final BookmarkablePageLink<String> forwardLink = new BookmarkablePageLink<String>("forwardLink", ArticleViewPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return pageCount > currentPage;
			}

		};
		forwardLink.setParameter("0", contentId);
		forwardLink.setParameter("1", currentPage + 1);
		this.add(forwardLink);
	}
}
