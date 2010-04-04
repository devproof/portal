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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.entity.BlogTagEntity;
import org.devproof.portal.module.blog.panel.BlogSearchBoxPanel;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.devproof.portal.module.blog.service.BlogService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class BlogPage extends BlogBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    @SpringBean(name = "blogDataProvider")
    private QueryDataProvider<BlogEntity, BlogQuery> blogDataProvider;
    @SpringBean(name = "blogTagService")
    private TagService<BlogTagEntity> blogTagService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    private BlogDataView dataView;
    private IModel<BlogQuery> queryModel;
    private PageParameters params;

    public BlogPage(PageParameters params) {
        super(params);
        this.params = params;
        this.queryModel = blogDataProvider.getSearchQueryModel();
        add(createBlogDataView());
        add(createPagingPanel());
        addFilterBox(createBlogSearchBoxPanel());
        addTagCloudBox();
    }

    private BlogSearchBoxPanel createBlogSearchBoxPanel() {
        return new BlogSearchBoxPanel("box", queryModel);
    }

    private BookmarkablePagingPanel createPagingPanel() {
        return new BookmarkablePagingPanel("paging", dataView, queryModel, BlogPage.class);
    }

    private void addTagCloudBox() {
        addTagCloudBox(blogTagService, BlogPage.class);
    }

    private BlogDataView createBlogDataView() {
        dataView = new BlogDataView("listBlog");
        return dataView;
    }

    private BlogView createBlogView(Item<BlogEntity> item) {
        return new BlogView("blogView", item);
    }


    @Override
    public String getPageTitle() {
        if(blogDataProvider.size() == 1) {
            Iterator<? extends BlogEntity> it = blogDataProvider.iterator(0, 1);
            BlogEntity blog = it.next();
            return blog.getHeadline();
        }
        return "";
    }

    private class BlogDataView extends AutoPagingDataView<BlogEntity> {
        private static final long serialVersionUID = 1L;

        public BlogDataView(String id) {
            super(id, blogDataProvider);
            setItemsPerPage(configurationService.findAsInteger(BlogConstants.CONF_BLOG_ENTRIES_PER_PAGE));
            setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        }

        @Override
        protected void populateItem(Item<BlogEntity> item) {
            item.add(createBlogView(item));
            item.setOutputMarkupId(true);
        }
    }

    public class BlogView extends Fragment {

        private static final long serialVersionUID = 1L;

        private IModel<BlogEntity> blogModel;

        public BlogView(String id, Item<BlogEntity> item) {
            super(id, "blogView", BlogPage.this);
            blogModel = item.getModel();
            add(createAppropriateAuthorPanel(item));
            add(createHeadline());
            add(createMetaInfoPanel());
            add(createPrintLink());
            add(createTagPanel());
            add(createContentLabel());
            add(createCommentPanel());
        }

        private Component createPrintLink() {
            BlogEntity blog = blogModel.getObject();
            BookmarkablePageLink<BlogPrintPage> link = new BookmarkablePageLink<BlogPrintPage>("printLink",
                    BlogPrintPage.class, new PageParameters("0=" + blog.getId()));
            link.add(createPrintImage());
            return link;
        }

        private Component createPrintImage() {
            return new Image("printImage", PrintConstants.REF_PRINTER_IMG);
        }

        private Component createAppropriateAuthorPanel(Item<BlogEntity> item) {
            if (isAuthor()) {
                return createAuthorPanel(item);
            } else {
                return createEmptyAuthorPanel();
            }
        }

        private AuthorPanel<BlogEntity> createAuthorPanel(final Item<BlogEntity> item) {
            return new AuthorPanel<BlogEntity>("authorButtons", blogModel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    blogService.delete(getEntityModel().getObject());
                    item.setVisible(false);
                    target.addComponent(item);
                    target.addComponent(getFeedback());
                    info(getString("msg.deleted"));
                }

                @Override
                public void onEdit(AjaxRequestTarget target) {
                    setResponsePage(new BlogEditPage(blogModel));
                }
            };
        }

        private WebMarkupContainer createEmptyAuthorPanel() {
            return new WebMarkupContainer("authorButtons");
        }

        private BookmarkablePageLink<BlogPage> createHeadline() {
            BookmarkablePageLink<BlogPage> headlineLink = new BookmarkablePageLink<BlogPage>("headlineLink",
                    BlogPage.class);
            if (params == null || !params.containsKey("id")) {
                BlogEntity blog = blogModel.getObject();
                headlineLink.setParameter("id", blog.getId());
            }
            headlineLink.add(headlineLinkLabel());
            return headlineLink;
        }

        private Label headlineLinkLabel() {
            IModel<String> headliineModel = new PropertyModel<String>(blogModel, "headline");
            return new Label("headlineLabel", headliineModel);
        }

        private MetaInfoPanel<BlogEntity> createMetaInfoPanel() {
            return new MetaInfoPanel<BlogEntity>("metaInfo", blogModel);
        }

        private ExtendedLabel createContentLabel() {
            IModel<String> contentModel = new PropertyModel<String>(blogModel, "content");
            return new ExtendedLabel("content", contentModel);
        }

        private Component createCommentPanel() {
            BlogEntity blog = blogModel.getObject();
            DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
            conf.setModuleContentId(blog.getId().toString());
            conf.setModuleName(BlogPage.class.getSimpleName());
            conf.setViewRights(blog.getCommentViewRights());
            conf.setWriteRights(blog.getCommentWriteRights());
            return new ExpandableCommentPanel("comments", conf);
        }

        private ContentTagPanel<BlogTagEntity> createTagPanel() {
            IModel<List<BlogTagEntity>> blogTagModel = new PropertyModel<List<BlogTagEntity>>(blogModel, "tags");
            return new ContentTagPanel<BlogTagEntity>("tags", blogTagModel, BlogPage.class);
        }
    }
}
