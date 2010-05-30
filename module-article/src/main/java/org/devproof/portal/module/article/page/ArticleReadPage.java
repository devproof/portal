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
package org.devproof.portal.module.article.page;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
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
	private IModel<ArticlePageEntity> displayedPageModel;
	private int currentPageNumber;
	private int numberOfPages;
	private String contentId;

	public ArticleReadPage(PageParameters params) {
		super(params);
		this.params = params;
		this.contentId = getContentId();
		this.currentPageNumber = getCurrentPageNumber();
		this.numberOfPages = getPageCount();
		this.displayedPageModel = createDisplayedPageModel();
		add(createTitleLabel());
		add(createMetaInfoPanel());
		add(createPrintLink());
		add(createAppropriateAuthorPanel());
		add(createTagPanel());
		add(createContentLabel());
		add(createBackLink());
		add(createForwardLink());
		add(createCommentPanel());
		addTagCloudBox();
	}

	private String getContentId() {
		String contentId = params.getString("0");
		if (contentId == null) {
			contentId = getRequest().getParameter("optparam");
		}
		return contentId;
	}

	private int getPageCount() {
		return (int) articleService.getPageCount(contentId);
	}

	private int getCurrentPageNumber() {
		return this.params.getAsInteger("1", 1);
	}

	@Override
	public String getPageTitle() {
		ArticlePageEntity displayedPage = displayedPageModel.getObject();
		return displayedPage.getArticle().getTitle();
	}

	@Override
	protected void onBeforeRender() {
		validateAccessRights();
		super.onBeforeRender();
	}

	private Component createCommentPanel() {
		DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
		ArticlePageEntity articlePage = displayedPageModel.getObject();
		if(articlePage != null) {
			ArticleEntity article = articlePage.getArticle();
			conf.setModuleContentId(article.getId().toString());
			conf.setModuleName(ArticlePage.class.getSimpleName());
			conf.setViewRights(article.getCommentViewRights());
			conf.setWriteRights(article.getCommentWriteRights());
			return new ExpandableCommentPanel("comments", conf);
		}
		return new WebMarkupContainer("comments");
	}

	private IModel<ArticlePageEntity> createDisplayedPageModel() {
		return new LoadableDetachableModel<ArticlePageEntity>() {
			private static final long serialVersionUID = 5844734752344587663L;

			@Override
			protected ArticlePageEntity load() {
				return articleService.findArticlePageByContentIdAndPage(contentId, currentPageNumber);
			}
		};
	}

	private void addTagCloudBox() {
		addTagCloudBox(articleTagService, ArticlePage.class);
	}

	private void validateAccessRights() {
		ArticlePageEntity displayedPage = displayedPageModel.getObject();
		PortalSession session = (PortalSession) getSession();
		if (displayedPage == null) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("error.page")));
		} else if (!session.hasRight("article.read") && !session.hasRight(displayedPage.getArticle().getReadRights())) {
			throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right"),
					getRequestURL()));
		}
	}

	private Label createTitleLabel() {
		IModel<String> titleModel = new PropertyModel<String>(displayedPageModel, "article.title");
		return new Label("title", titleModel);
	}

	private MetaInfoPanel<?> createMetaInfoPanel() {
		IModel<ArticleEntity> articleModel = new PropertyModel<ArticleEntity>(displayedPageModel, "article");
		return new MetaInfoPanel<ArticleEntity>("metaInfo", articleModel);
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
			return createAuthorPanel();
		} else {
			return createEmptyAuthorPanel();
		}
	}

	private Component createAuthorPanel() {
		AuthorPanel<ArticleEntity> authorPanel = newAuthorPanel();
		authorPanel.setRedirectPage(ArticlePage.class, new PageParameters("infoMsg=" + getString("msg.deleted")));
		return authorPanel;
	}

	private AuthorPanel<ArticleEntity> newAuthorPanel() {
		final IModel<ArticleEntity> articleModel = new PropertyModel<ArticleEntity>(displayedPageModel, "article");
		return new AuthorPanel<ArticleEntity>("authorButtons", articleModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDelete(AjaxRequestTarget target) {
				ArticleEntity article = articleModel.getObject();
				articleService.delete(article);
			}

			@Override
			public void onEdit(AjaxRequestTarget target) {
				IModel<ArticleEntity> articleModel = createArticleModel();
				setResponsePage(new ArticleEditPage(articleModel));
			}

			private IModel<ArticleEntity> createArticleModel() {
				return new LoadableDetachableModel<ArticleEntity>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected ArticleEntity load() {
						ArticleEntity article = articleModel.getObject();
						return articleService.findById(article.getId());
					}
				};
			}
		};
	}

	private Component createEmptyAuthorPanel() {
		EmptyPanel panel = new EmptyPanel("authorButtons");
		panel.setVisible(false);
		return panel;
	}

	private ExtendedLabel createContentLabel() {
		IModel<String> contentModel = new PropertyModel<String>(displayedPageModel, "content");
		return new ExtendedLabel("content", contentModel);
	}

	private TagContentPanel<ArticleTagEntity> createTagPanel() {
		IModel<List<ArticleTagEntity>> tagModel = new PropertyModel<List<ArticleTagEntity>>(displayedPageModel,
				"article.tags");
		return new TagContentPanel<ArticleTagEntity>("tags", tagModel, ArticlePage.class);
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
