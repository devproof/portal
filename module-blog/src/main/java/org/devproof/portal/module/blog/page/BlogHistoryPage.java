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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.*;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
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
@ModulePage(mountPath = "/blog/history")
public class BlogHistoryPage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    @SpringBean(name = "blogHistoryDataProvider")
    private QueryDataProvider<BlogHistorized, BlogHistoryQuery> blogHistoryDataProvider;
    @SpringBean(name = "displayDateFormat")
    private SimpleDateFormat dateFormat;
    private IModel<BlogHistoryQuery> queryModel;
    private ModalWindow modalWindow;
    private WebMarkupContainer blogRefreshTableContainer;
    private BlogHistoryDataView blogHistoryDataView;

    public BlogHistoryPage(PageParameters params) {
        super(params);
        this.queryModel = blogHistoryDataProvider.getSearchQueryModel();
        Blog blog = blogService.findById(params.getAsInteger("id"));
        this.queryModel.getObject().setBlog(blog);
        addSyntaxHighlighter();
        add(createModalWindow());
        add(createBlogRefreshContainer());
    }


    private ModalWindow createModalWindow() {
        modalWindow = new ModalWindow("modalWindow");
        return modalWindow;
    }

    private WebMarkupContainer createBlogRefreshContainer() {
        blogRefreshTableContainer = new WebMarkupContainer("refreshTable");
        blogRefreshTableContainer.add(createVersionNumberTableOrder());
        blogRefreshTableContainer.add(createModifiedByTableOrder());
        blogRefreshTableContainer.add(createModifiedAtTableOrder());
        blogRefreshTableContainer.add(createActionAtTableOrder());
        blogRefreshTableContainer.add(createActionTableOrder());
        blogRefreshTableContainer.add(createRepeatingHistory());
        blogRefreshTableContainer.add(createPageNavigatorTop());
        blogRefreshTableContainer.add(createPageNavigatorBottom());
        blogRefreshTableContainer.setOutputMarkupId(true);
        return blogRefreshTableContainer;
    }

    private PagingNavigator createPageNavigatorBottom() {
        return new PagingNavigator("navigatorBottom", blogHistoryDataView);
    }

    private PagingNavigator createPageNavigatorTop() {
        return new PagingNavigator("navigatorTop", blogHistoryDataView);
    }

    private BlogHistoryDataView createRepeatingHistory() {
        blogHistoryDataView = new BlogHistoryDataView("repeatingHistory", blogHistoryDataProvider);
        return blogHistoryDataView;
    }

    private OrderByBorder createActionTableOrder() {
        return new OrderByBorder("table_action", "action", blogHistoryDataProvider);
    }

    private OrderByBorder createActionAtTableOrder() {
        return new OrderByBorder("table_actionat", "actionAt", blogHistoryDataProvider);
    }

    private OrderByBorder createModifiedAtTableOrder() {
        return new OrderByBorder("table_modifiedat", "modifiedAt", blogHistoryDataProvider);
    }

    private OrderByBorder createModifiedByTableOrder() {
        return new OrderByBorder("table_modifiedby", "modifiedBy", blogHistoryDataProvider);
    }

    private OrderByBorder createVersionNumberTableOrder() {
        return new OrderByBorder("table_versionnumber", "versionNumber", blogHistoryDataProvider);
    }

    private class BlogHistoryDataView extends DataView<BlogHistorized> {
        private static final long serialVersionUID = 1L;

        public BlogHistoryDataView(String id, IDataProvider<BlogHistorized> dataProvider) {
            super(id, dataProvider);
            setItemsPerPage(50);
        }

        @Override
        protected void populateItem(Item<BlogHistorized> item) {
            IModel<BlogHistorized> blogHistorizedModel = item.getModel();
            item.add(createVersionNumberLabel(blogHistorizedModel));
            item.add(createModifiedByLabel(blogHistorizedModel));
            item.add(createModifiedAtLabel(blogHistorizedModel));
            item.add(createActionAtLabel(blogHistorizedModel));
            item.add(createActionLabel(blogHistorizedModel));
            item.add(createViewLink(blogHistorizedModel));
            item.add(createRestoreLink(blogHistorizedModel));
            item.add(createAlternatingModifier(item));
        }

        private Link<Void> createRestoreLink(final IModel<BlogHistorized> blogHistorizedModel) {
            return new Link<Void>("restore") {
                private static final long serialVersionUID = 6780596346704309571L;

                @Override
                public void onClick() {
                    blogService.restoreFromHistory(blogHistorizedModel.getObject());
                    info("Restored!!");
                }
            };
        }

        private Component createViewLink(final IModel<BlogHistorized> blogHistorizedModel) {
            return new AjaxLink<Void>("view") {
                private static final long serialVersionUID = -8563567205274304661L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    // TODO fix me
                    Blog blog = blogHistorizedModel.getObject().toBlog();
                    modalWindow.setContent(new BlogPanel(modalWindow.getContentId(), Model.of(blog)));
                    modalWindow.show(target);
                }
            };
        }

        private Label createVersionNumberLabel(IModel<BlogHistorized> blogHistorizedModel) {
            IModel<Integer> versionNumberModel = new PropertyModel<Integer>(blogHistorizedModel, "versionNumber");
            return new Label("versionNumber", versionNumberModel);
        }

        private Label createModifiedAtLabel(IModel<BlogHistorized> blogHistorizedModel) {
            IModel<String> modifiedAtModel = new PropertyModel<String>(blogHistorizedModel, "modifiedAt");
            return new Label("modifiedAt", modifiedAtModel);
        }

        private Label createModifiedByLabel(IModel<BlogHistorized> blogHistorizedModel) {
            IModel<String> modifiedByModel = new PropertyModel<String>(blogHistorizedModel, "modifiedBy");
            return new Label("modifiedBy", modifiedByModel);
        }

        private Label createActionAtLabel(IModel<BlogHistorized> blogHistorizedModel) {
            IModel<String> actionAtModel = new PropertyModel<String>(blogHistorizedModel, "actionAt");
            return new Label("actionAt", actionAtModel);
        }

        private Label createActionLabel(IModel<BlogHistorized> blogHistorizedModel) {
            IModel<String> actionModel = new PropertyModel<String>(blogHistorizedModel, "action");
            return new Label("action", actionModel);
        }
              
        private AttributeModifier createAlternatingModifier(final Item<BlogHistorized> item) {
            return new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getObject() {
                    return (item.getIndex() % 2 != 0) ? "even" : "odd";
                }
            });
        }
    }
}
