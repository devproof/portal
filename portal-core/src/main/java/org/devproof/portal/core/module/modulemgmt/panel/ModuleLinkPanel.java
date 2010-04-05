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
package org.devproof.portal.core.module.modulemgmt.panel;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.service.RegistryService;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;
import org.devproof.portal.core.module.modulemgmt.query.ModuleLinkQuery;
import org.devproof.portal.core.module.modulemgmt.service.ModuleService;

/**
 * @author Carsten Hufe
 */
public class ModuleLinkPanel extends Panel {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "moduleLinkDataProvider")
    private QueryDataProvider<ModuleLinkEntity, ModuleLinkQuery> moduleLinkDataProvider;
    @SpringBean(name = "moduleService")
    private ModuleService moduleService;
    @SpringBean(name = "registryService")
    private RegistryService registryService;

    private LinkType linkType;
    private WebMarkupContainer refreshTable;

    public ModuleLinkPanel(String id, LinkType linkType) {
        super(id);
        this.linkType = linkType;
        setLinkTypeInQuery();
        add(createLinkTypeTitleLabel());
        add(createModuleLinkRefreshTable());
    }

    private void setLinkTypeInQuery() {
        IModel<ModuleLinkQuery> searchQueryModel = moduleLinkDataProvider.getSearchQueryModel();
        ModuleLinkQuery query = searchQueryModel.getObject();
        query.setLinkType(linkType);
    }

    private Label createLinkTypeTitleLabel() {
        return new Label("naviTitle", getString(linkType.toString().toLowerCase()));
    }

    private WebMarkupContainer createModuleLinkRefreshTable() {
        refreshTable = new WebMarkupContainer("refreshTable");
        refreshTable.add(createModuleLinkForm());
        refreshTable.setOutputMarkupId(true);
        return refreshTable;
    }

    private Form<ModuleLinkEntity> createModuleLinkForm() {
        Form<ModuleLinkEntity> form = new Form<ModuleLinkEntity>("form");
        form.add(createModuleLinkView());
        return form;
    }

    private ModuleLinkView createModuleLinkView() {
        return new ModuleLinkView("tableRow", moduleLinkDataProvider);
    }

    private class ModuleLinkView extends DataView<ModuleLinkEntity> {
        private static final long serialVersionUID = 1L;

        public ModuleLinkView(String id, IDataProvider<ModuleLinkEntity> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<ModuleLinkEntity> item) {
            item.add(createSortLabel(item));
            item.add(createPageNameLabel(item));
            item.add(createModuleNameLabel(item));
            item.add(createVisibleCheckBox(item));
            item.add(createMoveUpLink(item));
            item.add(createMoveDownLink(item));
            item.add(createAlternatingModifier(item));
        }

        private AjaxLink<ModuleLinkEntity> createMoveUpLink(Item<ModuleLinkEntity> item) {
            AjaxLink<ModuleLinkEntity> upLink = newUpLink(item);
            upLink.add(createUpLinkImage());
            return upLink;
        }

        private Image createUpLinkImage() {
            return new Image("upImage", CommonConstants.REF_UP_IMG);
        }

        private AjaxLink<ModuleLinkEntity> newUpLink(final Item<ModuleLinkEntity> item) {
            return new AjaxLink<ModuleLinkEntity>("upLink") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    moduleService.moveUp(item.getModelObject());
                    registryService.rebuildRegistries(item.getModelObject().getLinkType());
                    target.addComponent(refreshTable);
                }
            };
        }

        private AjaxLink<ModuleLinkEntity> createMoveDownLink(Item<ModuleLinkEntity> item) {
            AjaxLink<ModuleLinkEntity> downLink = newDownLink(item);
            downLink.add(createDownLinkImage());
            return downLink;
        }

        private Image createDownLinkImage() {
            return new Image("downImage", CommonConstants.REF_DOWN_IMG);
        }

        private AjaxLink<ModuleLinkEntity> newDownLink(final Item<ModuleLinkEntity> item) {
            return new AjaxLink<ModuleLinkEntity>("downLink") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    moduleService.moveDown(item.getModelObject());
                    registryService.rebuildRegistries(item.getModelObject().getLinkType());
                    target.addComponent(refreshTable);
                }
            };
        }

        private Label createSortLabel(Item<ModuleLinkEntity> item) {
            return new Label("sort", Integer.toString(item.getModelObject().getSort()));
        }

        private Label createPageNameLabel(Item<ModuleLinkEntity> item) {
            return new Label("pageName", item.getModelObject().getPageName());
        }

        private Label createModuleNameLabel(Item<ModuleLinkEntity> item) {
            return new Label("moduleName", item.getModelObject().getModuleName());
        }

        private CheckBox createVisibleCheckBox(final Item<ModuleLinkEntity> item) {
            return new CheckBox("visible", new PropertyModel<Boolean>(item.getModelObject(), "visible")) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSelectionChanged(Object newSelection) {
                    moduleService.save(item.getModelObject());
                    registryService.rebuildRegistries(item.getModelObject().getLinkType());
                    setSelectionMessage(newSelection);
                }

                private void setSelectionMessage(Object newSelection) {
                    Boolean selection = (Boolean) newSelection;
                    if (selection) {
                        info(getString("msg.selected"));
                    } else {
                        info(getString("msg.deselected"));
                    }
                }

                @Override
                protected boolean wantOnSelectionChangedNotifications() {
                    return true;
                }
            };
        }

        private AttributeModifier createAlternatingModifier(final Item<ModuleLinkEntity> item) {
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
