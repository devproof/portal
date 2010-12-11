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
package org.devproof.portal.module.blog.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.panel.BlogPanel;
import org.devproof.portal.module.blog.panel.BlogSearchBoxPanel;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.devproof.portal.module.blog.service.BlogService;
import org.devproof.portal.module.blog.service.BlogTagService;

import java.util.Iterator;

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

    public BlogPage(PageParameters params) {
        super(params);
        this.params = params;
        this.queryModel = blogDataProvider.getSearchQueryModel();
        add(createRepeatingBlogEntries());
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

    private BlogDataView createRepeatingBlogEntries() {
        dataView = new BlogDataView("repeatingBlogEntries");
        return dataView;
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
            setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        }

        @Override
        protected void populateItem(Item<Blog> item) {
            item.add(createBlogPanel(item));
            item.setOutputMarkupId(true);
        }

        private BlogPanel createBlogPanel(Item<Blog> item) {
            return new BlogPanel("blog", item.getModel()) {
                @Override
                protected void onDeleted(AjaxRequestTarget target) {
                    info(getString("msg.deleted"));
                    target.addComponent(getFeedback());
                }
            };
        }
    }
}
