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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.panel.TagCloudBoxPanel;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogTag;
import org.devproof.portal.module.blog.panel.BlogSearchBoxPanel;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.devproof.portal.module.blog.service.BlogService;
import org.devproof.portal.module.blog.service.BlogTagService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.CommentLinkPanel;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/blog", registerMainNavigationLink = true, defaultStartPage = true)
public class BlogPage extends BlogBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    @SpringBean(name = "blogDataProvider")
    private QueryDataProvider<Blog, BlogQuery> blogDataProvider;
    @SpringBean(name = "blogTagService")
    private BlogTagService blogTagService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    private BlogDataView dataView;
    private IModel<BlogQuery> queryModel;
    private PageParameters params;
    private WebMarkupContainer refreshContainerBlogEntries;

    public BlogPage(PageParameters params) {
        super(params);
        this.params = params;
        this.queryModel = blogDataProvider.getSearchQueryModel();
        add(createRefreshContainerBlogEntries());
        add(createPagingPanel());
    }

    private WebMarkupContainer createRefreshContainerBlogEntries() {
        refreshContainerBlogEntries = new WebMarkupContainer("refreshContainerBlogEntries");
        refreshContainerBlogEntries.add(createRepeatingBlogEntries());
        refreshContainerBlogEntries.setOutputMarkupId(true);
        return refreshContainerBlogEntries;
    }

    @Override
    protected Component newTagCloudBox(String markupId) {
        return createTagCloudBox(markupId);
    }

    private Component createTagCloudBox(String markupId) {
        return new TagCloudBoxPanel<BlogTag>(markupId, blogTagService, getClass());
    }

    @Override
    protected Component newFilterBox(String markupId) {
        return createBlogSearchBoxPanel(markupId);
    }

    private BlogSearchBoxPanel createBlogSearchBoxPanel(String markupId) {
        return new BlogSearchBoxPanel(markupId, queryModel);
    }

    private BookmarkablePagingPanel createPagingPanel() {
        return new BookmarkablePagingPanel("paging", dataView, queryModel, BlogPage.class);
    }

    private BlogDataView createRepeatingBlogEntries() {
        dataView = new BlogDataView("repeatingBlogEntries");
        return dataView;
    }

    private BlogView createBlogView(Item<Blog> item) {
        return new BlogView("blogView", item);
    }


    @Override
    public String getPageTitle() {
        if (blogDataProvider.size() == 1) {
            Iterator<? extends Blog> it = blogDataProvider.iterator(0, 1);
            Blog blog = it.next();
            return blog.getHeadline();
        }
        return "";
    }

    private class BlogDataView extends AutoPagingDataView<Blog> {
        private static final long serialVersionUID = 1L;

        public BlogDataView(String id) {
            super(id, blogDataProvider);
            setItemsPerPage(configurationService.findAsInteger(BlogConstants.CONF_BLOG_ENTRIES_PER_PAGE));
        }

        @Override
        protected void populateItem(Item<Blog> item) {
            item.add(createBlogView(item));
        }
    }

    public class BlogView extends Fragment {

        private static final long serialVersionUID = 1L;

        private IModel<Blog> blogModel;
        private ExpandableCommentPanel commentsPanel;

        public BlogView(String id, Item<Blog> item) {
            super(id, "blogView", BlogPage.this);
            blogModel = item.getModel();
            add(createAppropriateAuthorPanel(item));
            add(createHeadline());
            add(createMetaInfoPanel());
            add(createPrintLink());
            add(createTagPanel());
            add(createContentLabel());
            add(createCommentLinkPanel());
            add(createCommentPanel());
        }

        private CommentLinkPanel createCommentLinkPanel() {
            return new CommentLinkPanel("commentsLink", createCommentConfiguration(blogModel.getObject())) {
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

        private Component createPrintLink() {
            Blog blog = blogModel.getObject();
            return new BookmarkablePageLink<BlogPrintPage>("printLink", BlogPrintPage.class, new PageParameters("id=" + blog.getId()));
        }

        private Component createAppropriateAuthorPanel(Item<Blog> item) {
            if (isAuthor()) {
                return createAuthorPanel(item);
            } else {
                return createEmptyAuthorPanel();
            }
        }

        private AuthorPanel<Blog> createAuthorPanel(final Item<Blog> item) {
            return new AuthorPanel<Blog>("authorButtons", blogModel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    blogService.delete(getEntityModel().getObject());
                    info(getString("msg.deleted"));
                    target.addComponent(refreshContainerBlogEntries);
                    target.addComponent(getFeedback());
                }

                @Override
                public void onEdit(AjaxRequestTarget target) {
                    setResponsePage(new BlogEditPage(blogModel));
                }

                @Override
                protected MarkupContainer newHistorizationLink(String markupId) {
                    return new BookmarkablePageLink<BlogHistoryPage>(markupId, BlogHistoryPage.class) {
                        private static final long serialVersionUID = 1918205848493398092L;

                        @Override
                        public PageParameters getPageParameters() {
                            PageParameters params = new PageParameters();
                            params.put("id", blogModel.getObject().getId());
                            return params;
                        }
                    };
                }
            };
        }

        private WebMarkupContainer createEmptyAuthorPanel() {
            return new WebMarkupContainer("authorButtons");
        }

        private BookmarkablePageLink<BlogPage> createHeadline() {
            BookmarkablePageLink<BlogPage> headlineLink = new BookmarkablePageLink<BlogPage>("headlineLink", BlogPage.class);
            if (params == null || !params.containsKey("id")) {
                Blog blog = blogModel.getObject();
                headlineLink.setParameter("id", blog.getId());
            }
            headlineLink.add(headlineLinkLabel());
            return headlineLink;
        }

        private Label headlineLinkLabel() {
            IModel<String> headlineModel = new PropertyModel<String>(blogModel, "headline");
            return new Label("headlineLabel", headlineModel);
        }

        private MetaInfoPanel<Blog> createMetaInfoPanel() {
            return new MetaInfoPanel<Blog>("metaInfo", blogModel);
        }

        private ExtendedLabel createContentLabel() {
            IModel<String> contentModel = new PropertyModel<String>(blogModel, "content");
            return new ExtendedLabel("content", contentModel);
        }

        private Component createCommentPanel() {
            Blog blog = blogModel.getObject();
            DefaultCommentConfiguration conf = createCommentConfiguration(blog);
            commentsPanel = new ExpandableCommentPanel("comments", conf);
            return commentsPanel;
        }

        private DefaultCommentConfiguration createCommentConfiguration(Blog blog) {
            DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
            conf.setModuleContentId(blog.getId().toString());
            conf.setModuleName(BlogPage.class.getSimpleName());
            conf.setViewRights(blog.getCommentViewRights());
            conf.setWriteRights(blog.getCommentWriteRights());
            return conf;
        }

        private TagContentPanel<BlogTag> createTagPanel() {
            IModel<List<BlogTag>> blogTagModel = new PropertyModel<List<BlogTag>>(blogModel, "tags");
            return new TagContentPanel<BlogTag>("tags", blogTagModel, BlogPage.class);
        }
    }
}
