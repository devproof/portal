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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.model.EntityModel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.devproof.portal.module.article.service.ArticleService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

/**
 * Article overview page
 * 
 * @author Carsten Hufe
 */
public class ArticleReadPage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "articleTagService")
	private TagService<ArticleTagEntity> articleTagService;

	private PageParameters params;
	private ArticlePageEntity displayedPage;
	private String contentId;
	private int currentPageNumber;
	private int numberOfPages;

	public ArticleReadPage(PageParameters params) {
		super(params);
		this.params = params;
		setContentId();
		setCurrentPageNumber();
		setNumberOfPages();
		setDisplayedPage();
		validateAccessRights();
		add(createTitleLabel());
		add(createMetaInfoPanel());
		add(createPrintLink());
		add(createAppropriateAuthorPanel());
		add(createTagPanel(params));
		add(createContentLabel());
		add(createBackLink());
		add(createForwardLink());
		add(createCommentPanel());
		addTagCloudBox();
		setPageTitle(displayedPage.getArticle().getTitle());
	}

	private Component createCommentPanel() {
		DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
		ArticleEntity article = displayedPage.getArticle();
		conf.setModuleContentId(article.getId().toString());
		conf.setModuleName(ArticlePage.class.getSimpleName());
		conf.setViewRights(article.getCommentViewRights());
		conf.setWriteRights(article.getCommentWriteRights());
		return new ExpandableCommentPanel("comments", conf);
	}

	private void setNumberOfPages() {
		numberOfPages = (int) articleService.getPageCount(contentId);
	}

	private void setDisplayedPage() {
		displayedPage = articleService.findArticlePageByContentIdAndPage(contentId, currentPageNumber);
	}

	private void addTagCloudBox() {
		addTagCloudBox(articleTagService, new PropertyModel<ArticleTagEntity>(new ArticleQuery(), "tag"),
				ArticlePage.class, params);
	}

	private void setCurrentPageNumber() {
		currentPageNumber = params.getAsInteger("1", 1);
	}

	private void setContentId() {
		contentId = params.getString("0");
		if (contentId == null) {
			contentId = getRequest().getParameter("optparam");
		}
	}

	private void validateAccessRights() {
		PortalSession session = (PortalSession) getSession();
		if (displayedPage == null) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("error.page")));
		} else if (!session.hasRight("article.read") && !session.hasRight(displayedPage.getArticle().getReadRights())) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right"),
					getRequestURL()));
		}
	}

	private Label createTitleLabel() {
		return new Label("title", displayedPage.getArticle().getTitle());
	}

	private MetaInfoPanel createMetaInfoPanel() {
		return new MetaInfoPanel("metaInfo", displayedPage.getArticle());
	}

	private Component createPrintLink() {
		BookmarkablePageLink<ArticlePrintPage> link = new BookmarkablePageLink<ArticlePrintPage>("printLink",
				ArticlePrintPage.class, new PageParameters("0=" + contentId));
		link.add(createPrintImage());
		return link;
	}

	private Component createPrintImage() {
		return new Image("printImage", PrintConstants.REF_PRINTER_IMG);
	}

	private Component createAppropriateAuthorPanel() {
		if (isAuthor()) {
			return createAuthorPanel(displayedPage);
		} else {
			return createEmptyAuthorPanel();
		}
	}

	private Component createAuthorPanel(ArticlePageEntity page) {
		AuthorPanel<ArticleEntity> authorPanel = newAuthorPanel(page);
		authorPanel.setRedirectPage(ArticlePage.class, new PageParameters("infoMsg=" + getString("msg.deleted")));
		return authorPanel;
	}

	private AuthorPanel<ArticleEntity> newAuthorPanel(final ArticlePageEntity page) {
		AuthorPanel<ArticleEntity> authorPanel = new AuthorPanel<ArticleEntity>("authorButtons", page.getArticle()) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDelete(AjaxRequestTarget target) {
				articleService.delete(page.getArticle());
			}

			@Override
			public void onEdit(AjaxRequestTarget target) {
				ArticleEntity article = page.getArticle();
				article = articleService.findByIdAndPrefetch(article.getId());
				IModel<ArticleEntity> articleModel = EntityModel.of(article);
				setResponsePage(new ArticleEditPage(articleModel));
			}
		};
		return authorPanel;
	}

	private Component createEmptyAuthorPanel() {
		return new EmptyPanel("authorButtons").setVisible(false);
	}

	private ExtendedLabel createContentLabel() {
		return new ExtendedLabel("content", displayedPage.getContent());
	}

	private ContentTagPanel<ArticleTagEntity> createTagPanel(PageParameters params) {
		return new ContentTagPanel<ArticleTagEntity>("tags", new ListModel<ArticleTagEntity>(displayedPage.getArticle()
				.getTags()), ArticlePage.class, params);
	}

	private BookmarkablePageLink<String> createForwardLink() {
		BookmarkablePageLink<String> forwardLink = newForwardLink();
		forwardLink.setParameter("0", contentId);
		forwardLink.setParameter("1", currentPageNumber + 1);
		return forwardLink;
	}

	private BookmarkablePageLink<String> newForwardLink() {
		return new BookmarkablePageLink<String>("forwardLink", ArticleReadPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return numberOfPages > currentPageNumber;
			}

		};
	}

	private BookmarkablePageLink<String> createBackLink() {
		BookmarkablePageLink<String> backLink = newBackLink();
		backLink.setParameter("0", contentId);
		backLink.setParameter("1", currentPageNumber - 1);
		return backLink;
	}

	private BookmarkablePageLink<String> newBackLink() {
		return new BookmarkablePageLink<String>("backLink", ArticleReadPage.class) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return currentPageNumber > 1;
			}
		};
	}
}
