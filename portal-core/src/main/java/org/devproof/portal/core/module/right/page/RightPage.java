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
package org.devproof.portal.core.module.right.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.dao.DeleteFailedException;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.right.panel.RightEditPanel;
import org.devproof.portal.core.module.right.panel.RightSearchBoxPanel;
import org.devproof.portal.core.module.right.query.RightQuery;
import org.devproof.portal.core.module.right.service.RightService;

/**
 * @author Carsten Hufe
 */
public class RightPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "rightDataProvider")
    private QueryDataProvider<RightEntity, RightQuery> rightDataProvider;
    @SpringBean(name = "rightService")
    private RightService rightService;
    private WebMarkupContainer refreshTable;
    private BubblePanel bubblePanel;
    private IModel<RightQuery> queryModel;

    public RightPage(PageParameters params) {
        super(params);
        this.queryModel = rightDataProvider.getSearchQueryModel();
        add(createRightRefreshTableContainer());
        add(createBubblePanel());
        addPageAdminBoxLink(createCreateRightLink());
        addFilterBox(createRightSearchBoxPanel());
    }

    private RightSearchBoxPanel createRightSearchBoxPanel() {
        return new RightSearchBoxPanel("box", queryModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.addComponent(refreshTable);
            }
        };
    }

    private WebMarkupContainer createRightRefreshTableContainer() {
        refreshTable = new WebMarkupContainer("refreshTable");
        refreshTable.add(createRightNameTableOrder());
        refreshTable.add(createRightDescriptionTableOrder());
        refreshTable.add(createRightDataView());
        refreshTable.setOutputMarkupId(true);
        return refreshTable;
    }

    private RightDataView createRightDataView() {
        return new RightDataView("tableRow", rightDataProvider);
    }

    private OrderByBorder createRightDescriptionTableOrder() {
        return new OrderByBorder("table_description", "description", rightDataProvider);
    }

    private OrderByBorder createRightNameTableOrder() {
        return new OrderByBorder("table_right", "right", rightDataProvider);
    }

    private BubblePanel createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private AjaxLink<Void> createCreateRightLink() {
        AjaxLink<Void> adminLink = newCreateRightLink();
        adminLink.add(createRightLinkLabel());
        return adminLink;
    }

    private Label createRightLinkLabel() {
        return new Label("linkName", getString("createLink"));
    }

    private AjaxLink<Void> newCreateRightLink() {
        return new AjaxLink<Void>("adminLink") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                bubblePanel.setContent(createRightEditPanel());
                bubblePanel.showModal(target);
            }

            private RightEditPanel createRightEditPanel() {
                IModel<RightEntity> rightModel = Model.of(rightService.newRightEntity());
                return new RightEditPanel(bubblePanel.getContentId(), rightModel, true) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSave(AjaxRequestTarget target) {
                        rightService.refreshGlobalApplicationRights();
                        bubblePanel.hide(target);
                        info(getString("msg.saved"));
                        target.addComponent(refreshTable);
                        target.addComponent(RightPage.this.getFeedback());
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        bubblePanel.hide(target);
                    }
                };
            }
        };
    }

    private class RightDataView extends DataView<RightEntity> {
        private static final long serialVersionUID = 1L;

        public RightDataView(String id, IDataProvider<RightEntity> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<RightEntity> item) {
            item.add(createRightNameLabel(item));
            item.add(createRightDescriptionLabel(item));
            item.add(createAuthorPanel(item));
            item.add(createAlternatingModifier(item));
        }

        private Label createRightDescriptionLabel(Item<RightEntity> item) {
            return new Label("description", item.getModelObject().getDescription());
        }

        private Label createRightNameLabel(Item<RightEntity> item) {
            return new Label("right", item.getModelObject().getRight());
        }

        private AuthorPanel<RightEntity> createAuthorPanel(final Item<RightEntity> item) {
            return new AuthorPanel<RightEntity>("authorButtons", item.getModel()) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    try {
                        RightEntity right = item.getModelObject();
                        rightService.delete(right);
                        rightService.refreshGlobalApplicationRights();
                        target.addComponent(refreshTable);
                        target.addComponent(getFeedback());
                        info(getString("msg.deleted"));
                    } catch (DeleteFailedException e) {
                        error(getString("msg.deleteFailed"));
                    }
                }

                @Override
                public void onEdit(AjaxRequestTarget target) {
                    bubblePanel.setContent(createRightEditPanel(item.getModel()));
                    bubblePanel.showModal(target);
                }

                private RightEditPanel createRightEditPanel(final IModel<RightEntity> rightModel) {
                    return new RightEditPanel(bubblePanel.getContentId(), rightModel, false) {
                        private static final long serialVersionUID1 = 1L;

                        @Override
                        public void onSave(AjaxRequestTarget target) {
                            rightService.refreshGlobalApplicationRights();
                            bubblePanel.hide(target);
                            info(getString("msg.saved"));
                            target.addComponent(refreshTable);
                            target.addComponent(getFeedback());
                        }

                        @Override
                        public void onCancel(AjaxRequestTarget target) {
                            bubblePanel.hide(target);
                        }
                    };
                }

            };
        }

        private AttributeModifier createAlternatingModifier(final Item<RightEntity> item) {
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
