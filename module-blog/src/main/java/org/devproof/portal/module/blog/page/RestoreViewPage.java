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
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogHistorized;
import org.devproof.portal.module.blog.panel.BlogPanel;
import org.devproof.portal.module.blog.panel.BlogSearchBoxPanel;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.devproof.portal.module.blog.service.BlogService;
import org.devproof.portal.module.blog.service.BlogTagService;

import java.util.Iterator;

/**
 * @author Carsten Hufe
 */
// TODO unit test
public abstract class RestoreViewPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    @SpringBean(name = "blogDataProvider")
    private QueryDataProvider<Blog, BlogQuery> blogDataProvider;
    @SpringBean(name = "blogTagService")
    private BlogTagService blogTagService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;

    // TODO darf nicht instanziierbar sein, wird abstract
    public RestoreViewPage() {
        super(new PageParameters());
        add(createBlogPanel("blog"));
    }

    protected abstract BlogPanel createBlogPanel(String markupId);
}
