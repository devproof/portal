/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.mount.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.modulemgmt.ModuleMgmtConstants;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.service.MountService;

/**
 * @author Carsten Hufe
 */
@Secured(ModuleMgmtConstants.ADMIN_RIGHT)
@ModulePage(mountPath = "/admin/mountpoint", registerGlobalAdminLink = true)
public class MountMgmtPage extends TemplatePage {
    private static final long serialVersionUID = 1L;

    @SpringBean(name = "mountPointDataProvider")
    private ISortableDataProvider<MountPoint> mountPointsDataProvider;
    @SpringBean(name = "mountService")
    private MountService mountService;
    private BubblePanel bubblePanel;
    private WebMarkupContainer mountRefreshTableContainer;
    private MountPointDataView mountDataView;

    public MountMgmtPage(PageParameters params) {
        super(params);
        add(createBubblePanel());
        add(createMountPointRefreshContainer());
    }

    private BubblePanel createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private WebMarkupContainer createMountPointRefreshContainer() {
        mountRefreshTableContainer = new WebMarkupContainer("refreshTable");
        mountRefreshTableContainer.add(createMountPathTableOrder());
        mountRefreshTableContainer.add(createHandlerKeyTableOrder());
        mountRefreshTableContainer.add(createRelatedContentIdTableOrder());
        mountRefreshTableContainer.add(createDefaultUrlTableOrder());
        mountRefreshTableContainer.add(createRepeatingMountPaths());
        mountRefreshTableContainer.add(createPageNavigatorTop());
        mountRefreshTableContainer.add(createPageNavigatorBottom());
        mountRefreshTableContainer.setOutputMarkupId(true);
        return mountRefreshTableContainer;
    }

    private PagingNavigator createPageNavigatorBottom() {
        return new PagingNavigator("navigatorBottom", mountDataView);
    }

    private PagingNavigator createPageNavigatorTop() {
        return new PagingNavigator("navigatorTop", mountDataView);
    }

    private MountPointDataView createRepeatingMountPaths() {
        mountDataView = new MountPointDataView("repeatingMountPoints", mountPointsDataProvider);
        return mountDataView;
    }

    private OrderByBorder createMountPathTableOrder() {
        return new OrderByBorder("table_mountPath", "mountPath", mountPointsDataProvider);
    }

    private OrderByBorder createRelatedContentIdTableOrder() {
        return new OrderByBorder("table_relatedContentId", "relatedContentId", mountPointsDataProvider);
    }

    private OrderByBorder createHandlerKeyTableOrder() {
        return new OrderByBorder("table_handlerKey", "handlerKey", mountPointsDataProvider);
    }

    private OrderByBorder createDefaultUrlTableOrder() {
        return new OrderByBorder("table_defaultUrl", "defaultUrl", mountPointsDataProvider);
    }

    private class MountPointDataView extends DataView<MountPoint> {
        private static final long serialVersionUID = 1L;

        public MountPointDataView(String id, IDataProvider<MountPoint> dataProvider) {
            super(id, dataProvider);
            setItemsPerPage(50);
        }

        @Override
        protected void populateItem(Item<MountPoint> item) {
            IModel<MountPoint> mountPointModel = item.getModel();
            item.add(createMountPathLabel(mountPointModel));
            item.add(createHandlerKeyLabel(mountPointModel));
            item.add(createRelatedContentIdLabel(mountPointModel));
            item.add(createDefaultUrlLabel(mountPointModel));
            item.add(createAuthorPanel(mountPointModel));
            item.add(createAlternatingModifier(item));
        }

        private Label createMountPathLabel(IModel<MountPoint> mountPointModel) {
            IModel<String> mountPathModel = new PropertyModel<String>(mountPointModel, "mountPath");
            return new Label("mountPath", mountPathModel);
        }

        private Label createHandlerKeyLabel(IModel<MountPoint> mountPointModel) {
            IModel<String> handlerKeyModel = new PropertyModel<String>(mountPointModel, "handlerKey");
            return new Label("handlerKey", handlerKeyModel);
        }

        private Label createRelatedContentIdLabel(IModel<MountPoint> mountPointModel) {
            IModel<String> relatedContentIdModel = new PropertyModel<String>(mountPointModel, "relatedContentId");
            return new Label("relatedContentId", relatedContentIdModel);
        }

        private Label createDefaultUrlLabel(IModel<MountPoint> mountPointModel) {
            return new Label("defaultUrl", createDefaultUrlModel(mountPointModel));
        }

        private IModel<String> createDefaultUrlModel(final IModel<MountPoint> mountPointModel) {
            return new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 5831214219470331468L;

                @Override
                public String getObject() {
                    MountPoint mountPoint = mountPointModel.getObject();
                    return getString("defaultUrl." + mountPoint.isDefaultUrl());
                }
            };
        }

        private AuthorPanel<MountPoint> createAuthorPanel(final IModel<MountPoint> mountPointModel) {
            return new AuthorPanel<MountPoint>("authorButtons", mountPointModel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    mountService.delete(mountPointModel.getObject());
                    target.addComponent(mountRefreshTableContainer);
                    target.addComponent(getFeedback());
                    info(getString("msg.deleted"));
                }

                @Override
                public boolean isEditButtonVisible() {
                    return false;
                }

                @Override
                public void onEdit(AjaxRequestTarget target) {
                    // do nothing
                }
            };
        }

        private AttributeModifier createAlternatingModifier(final Item<MountPoint> item) {
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
