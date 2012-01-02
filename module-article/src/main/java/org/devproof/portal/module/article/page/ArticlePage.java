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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleTag;
import org.devproof.portal.module.article.panel.ArticleSearchBoxPanel;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.devproof.portal.module.article.service.ArticleService;
import org.devproof.portal.module.article.service.ArticleTagService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.CommentLinkPanel;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/articles", registerMainNavigationLink = true)
public class ArticlePage extends ArticleBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "articleService")
    private ArticleService articleService;
    @SpringBean(name = "articleDataProvider")
    private QueryDataProvider<Article, ArticleQuery> articleDataProvider;
    @SpringBean(name = "articleTagService")
    private ArticleTagService articleTagService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    private ArticleDataView dataView;
    private IModel<ArticleQuery> searchQueryModel;
    private WebMarkupContainer refreshContainerArticles;
    private PageParameters params;

    public ArticlePage(PageParameters params) {
        super(params);
        this.params = params;
        searchQueryModel = articleDataProvider.getSearchQueryModel();
        add(createRefreshContainerArticles());
        add(createPagingPanel());
    }

    private WebMarkupContainer createRefreshContainerArticles() {
        refreshContainerArticles = new WebMarkupContainer("refreshContainerArticles");
        refreshContainerArticles.add(createRepeatingArticles());
        refreshContainerArticles.setOutputMarkupId(true);
        return refreshContainerArticles;
    }

    @Override
    protected Component newFilterBox(String markupId) {
        return createArticleSearchBoxPanel(markupId);
    }

    @Override
    protected Component newTagCloudBox(String markupId) {
        return createTagCloudBox(markupId);
    }

    private Component createTagCloudBox(String markupId) {
        return new TagCloudBoxPanel<ArticleTag>(markupId, params, articleTagService, getClass());
    }

    private ArticleSearchBoxPanel createArticleSearchBoxPanel(String markupId) {
        return new ArticleSearchBoxPanel(markupId, searchQueryModel);
    }

    private ArticleDataView createRepeatingArticles() {
        dataView = new ArticleDataView("repeatingArticles");
        return dataView;
    }

    private BookmarkablePagingPanel createPagingPanel() {
        return new BookmarkablePagingPanel("paging", dataView, searchQueryModel, ArticlePage.class);
    }

    @Override
    public String getPageTitle() {
        if (articleDataProvider.size() == 1) {
            Iterator<? extends Article> it = articleDataProvider.iterator(0, 1);
            Article article = it.next();
            return article.getTitle();
        }
        return "";
    }

    private class ArticleDataView extends AutoPagingDataView<Article> {
        private static final long serialVersionUID = 1L;

        public ArticleDataView(String id) {
            super(id, articleDataProvider);
            setItemsPerPage(configurationService.findAsInteger(ArticleConstants.CONF_ARTICLES_PER_PAGE));
			setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        }

        @Override
        protected void populateItem(Item<Article> item) {
            item.add(createArticleView(item));
        }

        private ArticleView createArticleView(Item<Article> item) {
            return new ArticleView("articleView", item);
        }
    }

    /**
     * Shows an article teaser
     */
    private class ArticleView extends Fragment {

        private static final long serialVersionUID = 1L;
        private IModel<Article> articleModel;
        private ExpandableCommentPanel commentsPanel;

        public ArticleView(String id, Item<Article> item) {
            super(id, "articleView", ArticlePage.this);
            articleModel = item.getModel();
            add(createAppropriateAuthorPanel(item));
            add(createTitleLink());
            add(createMetaInfoPanel());
            add(createPrintLink());
            add(createTeaserLabel());
            add(createTagPanel());
            add(createReadMoreLink());
            add(createCommentLinkPanel());
            add(createCommentPanel());
        }

        private CommentLinkPanel createCommentLinkPanel() {
            return new CommentLinkPanel("commentsLink", createCommentConfiguration()) {
                private static final long serialVersionUID = -4023802441634483395L;

                @Override
                protected boolean isCommentPanelVisible() {
                    return commentsPanel.isCommentsVisible();
                }

                @Override
                protected void onClick(AjaxRequestTarget target) {
                    commentsPanel.toggle(target);
                }
            };
        }

        private Component createCommentPanel() {
            DefaultCommentConfiguration conf = createCommentConfiguration();
            commentsPanel = new ExpandableCommentPanel("comments", conf);
            return commentsPanel;
        }

        private DefaultCommentConfiguration createCommentConfiguration() {
            Article article = articleModel.getObject();
            DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
            conf.setModuleContentId(article.getId().toString());
            conf.setModuleName(ArticlePage.class.getSimpleName());
            conf.setViewRights(article.getCommentViewRights());
            conf.setWriteRights(article.getCommentWriteRights());
            return conf;
        }

        private Component createPrintLink() {
            Article article = articleModel.getObject();
            PageParameters params = new PageParameters("0=" + article.getId());
            return new BookmarkablePageLink<ArticlePrintPage>("printLink", ArticlePrintPage.class, params) {
                private static final long serialVersionUID = 1289408920992789194L;

                @Override
                public boolean isVisible() {
                    return isAllowedToRead(articleModel);
                }
            };
        }

        private Component createAppropriateAuthorPanel(Item<Article> item) {
            if (isAuthor()) {
                return createAuthorPanel(item);
            } else {
                return createEmptyAuthorPanel();
            }
        }

        private BookmarkablePageLink<ArticleReadPage> createReadMoreLink() {
            Article article = articleModel.getObject();
            BookmarkablePageLink<ArticleReadPage> readMoreLink = newReadMoreLink();
            readMoreLink.add(createReadMoreLabel());
            readMoreLink.setParameter("0", article.getId());
            return readMoreLink;
        }

        private BookmarkablePageLink<ArticleReadPage> newReadMoreLink() {
            return new BookmarkablePageLink<ArticleReadPage>("readMoreLink", ArticleReadPage.class) {
                private static final long serialVersionUID = -4230704759939474616L;

                @Override
                public boolean isEnabled() {
                    return isAllowedToRead(articleModel);
                }
            };
        }

        private Label createReadMoreLabel() {
            IModel<String> readMoreModel = createReadMoreModel();
            return new Label("readMoreLabel", readMoreModel);
        }

        private AbstractReadOnlyModel<String> createReadMoreModel() {
            return new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 118766734564336104L;

                @Override
                public String getObject() {
                    String labelKey = isAllowedToRead(articleModel) ? "readMore" : "loginToReadMore";
                    return ArticlePage.this.getString(labelKey);
                }
            };
        }

        private TagContentPanel<ArticleTag> createTagPanel() {
            IModel<List<ArticleTag>> tagModel = new PropertyModel<List<ArticleTag>>(articleModel, "tags");
            return new TagContentPanel<ArticleTag>("tags", tagModel, ArticlePage.class);
        }

        private ExtendedLabel createTeaserLabel() {
            IModel<String> teaserModel = new PropertyModel<String>(articleModel, "teaser");
            return new ExtendedLabel("teaser", teaserModel);
        }

        private MetaInfoPanel<?> createMetaInfoPanel() {
            return new MetaInfoPanel<Article>("metaInfo", articleModel);
        }

        private WebMarkupContainer createEmptyAuthorPanel() {
            return new WebMarkupContainer("authorButtons");
        }

        private boolean isAllowedToRead(IModel<Article> articleModel) {
            Article article = articleModel.getObject();
            PortalSession session = (PortalSession) getSession();
            return session.hasRight("article.read") || session.hasRight(article.getReadRights());
        }

        private BookmarkablePageLink<ArticleReadPage> createTitleLink() {
            Article article = articleModel.getObject();
            BookmarkablePageLink<ArticleReadPage> titleLink = newTitleLink();
            titleLink.add(createTitleLabel());
            titleLink.setParameter("0", article.getId());
            return titleLink;
        }

        private BookmarkablePageLink<ArticleReadPage> newTitleLink() {
            return new BookmarkablePageLink<ArticleReadPage>("titleLink", ArticleReadPage.class) {
                private static final long serialVersionUID = -1166238358025078721L;

                @Override
                public boolean isEnabled() {
                    return isAllowedToRead(articleModel);
                }
            };
        }

        private Label createTitleLabel() {
            IModel<String> titleModel = new PropertyModel<String>(articleModel, "title");
            return new Label("titleLabel", titleModel);
        }

        private AuthorPanel<Article> createAuthorPanel(final Item<Article> item) {
            return new AuthorPanel<Article>("authorButtons", articleModel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    articleService.delete(getEntityModel().getObject());
                    info(getString("msg.deleted"));
                    target.addComponent(getFeedback());
                    target.addComponent(refreshContainerArticles);
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
                            params.add("id", articleModel.getObject().getId());
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
    }
}
