/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.article.page;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
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
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticlePage;
import org.devproof.portal.module.article.entity.ArticleTag;
import org.devproof.portal.module.article.service.ArticleService;
import org.devproof.portal.module.article.service.ArticleTagService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

import java.util.List;

/**
 * Article overview page
 * 
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/article", indexMountedPath = true)
public class ArticleReadPage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "articleTagService")
	private ArticleTagService articleTagService;

	private PageParameters params;
	private IModel<ArticlePage> displayedPageModel;
	private int currentPageNumber;
	private int numberOfPages;
	private Integer contentId;

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
	}

    @Override
    protected Component newTagCloudBox(String markupId) {
        return createTagCloudBox(markupId);
    }

    private Component createTagCloudBox(String markupId) {
        return new TagCloudBoxPanel<ArticleTag>(markupId, articleTagService, org.devproof.portal.module.article.page.ArticlePage.class);
    }

	private Integer getContentId() {
		String id = params.getString("0");
        contentId = Integer.valueOf(id);
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
		ArticlePage displayedPage = displayedPageModel.getObject();
		return displayedPage.getArticle().getTitle();
	}

	@Override
	protected void onBeforeRender() {
		validateAccessRights();
		super.onBeforeRender();
	}

	private Component createCommentPanel() {
		DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
		org.devproof.portal.module.article.entity.ArticlePage articlePage = displayedPageModel.getObject();
		if(articlePage != null) {
			Article article = articlePage.getArticle();
			conf.setModuleContentId(article.getId().toString());
			conf.setModuleName(org.devproof.portal.module.article.page.ArticlePage.class.getSimpleName());
			conf.setViewRights(article.getCommentViewRights());
			conf.setWriteRights(article.getCommentWriteRights());
			return new ExpandableCommentPanel("comments", conf);
		}
		return new WebMarkupContainer("comments");
	}

	private IModel<ArticlePage> createDisplayedPageModel() {
		return new LoadableDetachableModel<ArticlePage>() {
			private static final long serialVersionUID = 5844734752344587663L;

			@Override
			protected ArticlePage load() {
				return articleService.findArticlePageByArticleIdAndPage(contentId, currentPageNumber);
			}
		};
	}

	private void validateAccessRights() {
		ArticlePage displayedPage = displayedPageModel.getObject();
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
		IModel<Article> articleModel = new PropertyModel<Article>(displayedPageModel, "article");
		return new MetaInfoPanel<Article>("metaInfo", articleModel);
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
		AuthorPanel<Article> authorPanel = newAuthorPanel();
		authorPanel.setRedirectPage(org.devproof.portal.module.article.page.ArticlePage.class, new PageParameters("infoMsg=" + getString("msg.deleted")));
		return authorPanel;
	}

	private AuthorPanel<Article> newAuthorPanel() {
		final IModel<Article> articleModel = new PropertyModel<Article>(displayedPageModel, "article");
		return new AuthorPanel<Article>("authorButtons", articleModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public void onDelete(AjaxRequestTarget target) {
				Article article = articleModel.getObject();
				articleService.delete(article);
			}

			@Override
			public void onEdit(AjaxRequestTarget target) {
				IModel<Article> articleModel = createArticleModel();
				setResponsePage(new ArticleEditPage(articleModel));
			}


            @Override
            protected MarkupContainer newHistorizationLink(String markupId) {
                return new BookmarkablePageLink<ArticleHistoryPage>(markupId, ArticleHistoryPage.class) {
                    private static final long serialVersionUID = 1918205848493398092L;

                    @Override
                    public PageParameters getPageParameters() {
                        PageParameters params = new PageParameters();
                        params.put("id", articleModel.getObject().getId());
                        return params;
                    }
                };
            }

			private IModel<Article> createArticleModel() {
				return new LoadableDetachableModel<Article>() {
					private static final long serialVersionUID = 1L;

					@Override
					protected Article load() {
						Article article = articleModel.getObject();
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

	private TagContentPanel<ArticleTag> createTagPanel() {
		IModel<List<ArticleTag>> tagModel = new PropertyModel<List<ArticleTag>>(displayedPageModel,
				"article.tags");
		return new TagContentPanel<ArticleTag>("tags", tagModel, org.devproof.portal.module.article.page.ArticlePage.class);
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
