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

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.historization.page.HistoryPage;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogHistorized;
import org.devproof.portal.module.blog.panel.BlogPanel;
import org.devproof.portal.module.blog.query.BlogHistoryQuery;
import org.devproof.portal.module.blog.service.BlogService;

import java.text.SimpleDateFormat;

/**
 * @author Carsten Hufe
 */
// TODO german customization
// TODO generalize
// TODO rights
@ModulePage(mountPath = "/blog/history")
public class BlogHistoryPage extends HistoryPage<BlogHistorized> {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    @SpringBean(name = "blogHistoryDataProvider")
    private QueryDataProvider<BlogHistorized, BlogHistoryQuery> blogHistoryDataProvider;
    @SpringBean(name = "displayDateFormat")
    private SimpleDateFormat dateFormat;
    private IModel<BlogHistoryQuery> queryModel;

    public BlogHistoryPage(PageParameters params) {
        super(params);
        this.queryModel = blogHistoryDataProvider.getSearchQueryModel();
        Blog blog = blogService.findById(params.getAsInteger("id"));
        this.queryModel.getObject().setBlog(blog);
    }

    @Override
    protected QueryDataProvider<BlogHistorized, ?> getQueryDataProvider() {
        return blogHistoryDataProvider;
    }

    @Override
    protected Component newHistorizedView(String markupId, IModel<BlogHistorized> historizedModel) {
        return new BlogPanel(markupId, new PropertyModel<Blog>(historizedModel, "blog")) {
            private static final long serialVersionUID = -8580867738175014351L;

            @Override
            protected void onDeleted(AjaxRequestTarget target) {
                info(getString("msg.deleted"));
                target.addComponent(getFeedback());
            }

            @Override
            public boolean hideAuthorButtons() {
                return true;
            }

            @Override
            public boolean hideComments() {
                return true;
            }
        };
    }

    @Override
    protected void onRestore(IModel<BlogHistorized> restoreModel) {
        blogService.restoreFromHistory(restoreModel.getObject());
        error("hello world");
    }
}
