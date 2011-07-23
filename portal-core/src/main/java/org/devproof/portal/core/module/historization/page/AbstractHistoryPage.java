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
package org.devproof.portal.core.module.historization.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.historization.service.Historized;

import java.text.SimpleDateFormat;

/**
 * Base history page which lists the actions
 *
 * @author Carsten Hufe
 */
public abstract class AbstractHistoryPage<T extends Historized> extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "displayDateFormat")
    private SimpleDateFormat dateFormat;
    private WebMarkupContainer refreshTableContainer;
    private HistoryDataView historyDataView;

    public AbstractHistoryPage(PageParameters params) {
        super(params);
        addSyntaxHighlighter();
        add(createContentTitleLabel());
        add(createRefreshContainer());
    }

    /**
     * @return an instance of the query data provider, which provides data for the list
     */
    protected abstract QueryDataProvider<T, ?> getQueryDataProvider();

    /**
     * Component which will be displayed when clicking on the view button in the history list
     *
     * @param markupId        markupId for the component
     * @param historizedModel historized dataset
     * @return a component instance
     */
    protected abstract Component newHistorizedView(String markupId, IModel<T> historizedModel);

    /**
     * Event which gets executed when clicking on the restore link in the history list or on the restore button
     * in the view page
     *
     * @param historizedModel historized dataset
     */
    protected abstract void onRestore(IModel<T> historizedModel);

    /**
     * @return model will be displayed in the page title
     */
    protected IModel<String> newHeadlineModel() {
        return new StringResourceModel("contentTitle", this, null);
    }

    private Label createContentTitleLabel() {
        return new Label("contentTitle", newHeadlineModel());
    }

    private WebMarkupContainer createRefreshContainer() {
        refreshTableContainer = new WebMarkupContainer("refreshTable");
        refreshTableContainer.add(createVersionNumberTableOrder());
        refreshTableContainer.add(createModifiedByTableOrder());
        refreshTableContainer.add(createModifiedAtTableOrder());
        refreshTableContainer.add(createActionAtTableOrder());
        refreshTableContainer.add(createActionTableOrder());
        refreshTableContainer.add(createRepeatingHistory());
        refreshTableContainer.add(createPageNavigatorTop());
        refreshTableContainer.add(createPageNavigatorBottom());
        refreshTableContainer.setOutputMarkupId(true);
        return refreshTableContainer;
    }

    private PagingNavigator createPageNavigatorBottom() {
        return new PagingNavigator("navigatorBottom", historyDataView);
    }

    private PagingNavigator createPageNavigatorTop() {
        return new PagingNavigator("navigatorTop", historyDataView);
    }

    private HistoryDataView createRepeatingHistory() {
        historyDataView = new HistoryDataView("repeatingHistory", getQueryDataProvider());
        return historyDataView;
    }

    private OrderByBorder createActionTableOrder() {
        return new OrderByBorder("table_action", "action", getQueryDataProvider());
    }

    private OrderByBorder createActionAtTableOrder() {
        return new OrderByBorder("table_actionat", "actionAt", getQueryDataProvider());
    }

    private OrderByBorder createModifiedAtTableOrder() {
        return new OrderByBorder("table_modifiedat", "modifiedAt", getQueryDataProvider());
    }

    private OrderByBorder createModifiedByTableOrder() {
        return new OrderByBorder("table_modifiedby", "modifiedBy", getQueryDataProvider());
    }

    private OrderByBorder createVersionNumberTableOrder() {
        return new OrderByBorder("table_versionnumber", "versionNumber", getQueryDataProvider());
    }


    private class HistoryDataView extends DataView<T> {
        private static final long serialVersionUID = 1L;

        public HistoryDataView(String id, IDataProvider<T> dataProvider) {
            super(id, dataProvider);
            setItemsPerPage(50);
        }

        @Override
        protected void populateItem(Item<T> item) {
            IModel<T> historizedModel = item.getModel();
            item.add(createVersionNumberLabel(historizedModel));
            item.add(createModifiedByLabel(historizedModel));
            item.add(createModifiedAtLabel(historizedModel));
            item.add(createActionAtLabel(historizedModel));
            item.add(createActionLabel(historizedModel));
            item.add(createViewLink(historizedModel));
            item.add(createRestoreLink(historizedModel));
            item.add(createAlternatingModifier(item));
        }

        private Link<Void> createRestoreLink(final IModel<T> historizedModel) {
            return new Link<Void>("restore") {
                private static final long serialVersionUID = 6780596346704309571L;

                @Override
                public void onClick() {
                    onRestore(historizedModel);
                }
            };
        }

        private Component createViewLink(final IModel<T> historizedModel) {
            return new AjaxLink<Void>("view") {
                private static final long serialVersionUID = -8563567205274304661L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    setResponsePage(new AbstractRestoreViewPage() {
                        private static final long serialVersionUID = -4001522334346081561L;

                        @Override
                        protected Component newHistorizedView(String markupId) {
                            return AbstractHistoryPage.this.newHistorizedView(markupId, historizedModel);
                        }

                        @Override
                        protected void onRestore() {
                            AbstractHistoryPage.this.onRestore(historizedModel);
                        }

                        @Override
                        protected void onBack() {
                            setResponsePage(AbstractHistoryPage.this);
                        }
                    });
                }
            };
        }

        private Label createVersionNumberLabel(IModel<T> historizedModel) {
            IModel<Integer> versionNumberModel = new PropertyModel<Integer>(historizedModel, "versionNumber");
            return new Label("versionNumber", versionNumberModel);
        }

        private Label createModifiedAtLabel(IModel<T> historizedModel) {
            IModel<String> modifiedAtModel = new PropertyModel<String>(historizedModel, "modifiedAt");
            return new Label("modifiedAt", modifiedAtModel);
        }

        private Label createModifiedByLabel(IModel<T> historizedModel) {
            IModel<String> modifiedByModel = new PropertyModel<String>(historizedModel, "modifiedBy");
            return new Label("modifiedBy", modifiedByModel);
        }

        private Label createActionAtLabel(IModel<T> historizedModel) {
            IModel<String> actionAtModel = new PropertyModel<String>(historizedModel, "actionAt");
            return new Label("actionAt", actionAtModel);
        }

        private Component createActionLabel(final IModel<T> historizedModel) {
            IModel<String> actionModel = new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = -8373553102841554443L;

                @Override
                public String getObject() {
                    String action = historizedModel.getObject().getAction().toString();
                    return new StringResourceModel(action, AbstractHistoryPage.this, new PropertyModel<Integer>(historizedModel, "restoredFromVersion")).getString();
                }
            };
            return new Label("action", actionModel);
        }

        private AttributeModifier createAlternatingModifier(final Item<T> item) {
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
