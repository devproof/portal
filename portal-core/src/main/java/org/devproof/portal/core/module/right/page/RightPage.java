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
package org.devproof.portal.core.module.right.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.repository.DeleteFailedException;
import org.devproof.portal.core.module.right.RightConstants;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.panel.RightEditPanel;
import org.devproof.portal.core.module.right.panel.RightSearchBoxPanel;
import org.devproof.portal.core.module.right.query.RightQuery;
import org.devproof.portal.core.module.right.service.RightService;

/**
 * @author Carsten Hufe
 */
@Secured(RightConstants.ADMIN_RIGHT)
@ModulePage(mountPath = "/admin/rights", registerGlobalAdminLink = true)
public class RightPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "rightDataProvider")
    private QueryDataProvider<Right, RightQuery> rightDataProvider;
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
    }

    @Override
    protected Component newPageAdminBoxLink(String linkMarkupId, String labelMarkupId) {
        return createCreateRightLink(linkMarkupId, labelMarkupId);
    }

    @Override
    protected Component newFilterBox(String markupId) {
        return createRightSearchBoxPanel(markupId);
    }

    private RightSearchBoxPanel createRightSearchBoxPanel(String markupId) {
        return new RightSearchBoxPanel(markupId, queryModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.add(refreshTable);
            }
        };
    }

    private WebMarkupContainer createRightRefreshTableContainer() {
        refreshTable = new WebMarkupContainer("refreshTable");
        refreshTable.add(createRightNameTableOrder());
        refreshTable.add(createRightDescriptionTableOrder());
        refreshTable.add(createRepeatingRights());
        refreshTable.setOutputMarkupId(true);
        return refreshTable;
    }

    private RightDataView createRepeatingRights() {
        return new RightDataView("repeatingRights", rightDataProvider);
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

    private AjaxLink<Void> createCreateRightLink(String linkMarkupId, String labelMarkupId) {
        AjaxLink<Void> adminLink = newCreateRightLink(linkMarkupId);
        adminLink.add(createRightLinkLabel(labelMarkupId));
        return adminLink;
    }

    private Label createRightLinkLabel(String labelMarkupId) {
        return new Label(labelMarkupId, getString("createLink"));
    }

    private AjaxLink<Void> newCreateRightLink(String linkMarkupId) {
        return new AjaxLink<Void>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                bubblePanel.setContent(createRightEditPanel());
                bubblePanel.showModal(target);
            }

            private RightEditPanel createRightEditPanel() {
                IModel<Right> rightModel = Model.of(rightService.newRightEntity());
                return new RightEditPanel(bubblePanel.getContentId(), rightModel, true) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSave(AjaxRequestTarget target) {
                        rightService.refreshGlobalApplicationRights();
                        bubblePanel.hide(target);
                        info(getString("msg.saved"));
                        target.add(refreshTable);
                        target.add(RightPage.this.getFeedback());
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        bubblePanel.hide(target);
                    }
                };
            }
        };
    }

    private class RightDataView extends DataView<Right> {
        private static final long serialVersionUID = 1L;

        public RightDataView(String id, IDataProvider<Right> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<Right> item) {
            item.add(createRightNameLabel(item));
            item.add(createRightDescriptionLabel(item));
            item.add(createAuthorPanel(item));
            item.add(createAlternatingModifier(item));
        }

        private Label createRightDescriptionLabel(Item<Right> item) {
            return new Label("description", item.getModelObject().getDescription());
        }

        private Label createRightNameLabel(Item<Right> item) {
            return new Label("right", item.getModelObject().getRight());
        }

        private AuthorPanel<Right> createAuthorPanel(final Item<Right> item) {
            return new AuthorPanel<Right>("authorButtons", item.getModel()) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    try {
                        Right right = item.getModelObject();
                        rightService.delete(right);
                        rightService.refreshGlobalApplicationRights();
                        target.add(refreshTable);
                        target.add(getFeedback());
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

                private RightEditPanel createRightEditPanel(final IModel<Right> rightModel) {
                    return new RightEditPanel(bubblePanel.getContentId(), rightModel, false) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onSave(AjaxRequestTarget target) {
                            rightService.refreshGlobalApplicationRights();
                            bubblePanel.hide(target);
                            info(getString("msg.saved"));
                            target.add(refreshTable);
                            target.add(getFeedback());
                        }

                        @Override
                        public void onCancel(AjaxRequestTarget target) {
                            bubblePanel.hide(target);
                        }
                    };
                }

            };
        }

        private AttributeModifier createAlternatingModifier(final Item<Right> item) {
            return AttributeModifier.replace("class", new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getObject() {
                    return (item.getIndex() % 2 != 0) ? "even" : "odd";
                }
            });
        }
    }

}
