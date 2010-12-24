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
package org.devproof.portal.core.module.role.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.config.Secured;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.ConfirmDeletePanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.right.RightConstants;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.RoleConstants;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.role.panel.RoleEditPanel;
import org.devproof.portal.core.module.role.panel.RoleSearchBoxPanel;
import org.devproof.portal.core.module.role.query.RoleQuery;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
@Secured(RoleConstants.ADMIN_RIGHT)
@ModulePage(mountPath = "/admin/roles", registerGlobalAdminLink = true)
public class RolePage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "roleDataProvider")
    private QueryDataProvider<Role, RoleQuery> roleDataProvider;
    @SpringBean(name = "roleService")
    private RoleService roleService;
    @SpringBean(name = "userService")
    private UserService userService;
    @SpringBean(name = "rightService")
    private RightService rightService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private IModel<RoleQuery> queryModel;
    private WebMarkupContainer refreshTable;
    private BubblePanel bubblePanel;

    public RolePage(PageParameters params) {
        super(params);
        this.queryModel = roleDataProvider.getSearchQueryModel();
        add(createRoleTableRefreshContainer());
        add(createBubblePanel());
        addPageAdminBoxLink(createCreateRoleLink());
        addFilterBox(createSearchBoxPanel());
    }

    private WebMarkupContainer createRoleTableRefreshContainer() {
        refreshTable = new WebMarkupContainer("refreshTable");
        refreshTable.add(createRoleDescriptionTableOrder());
        refreshTable.add(createRoleActiveTableOrder());
        refreshTable.add(createRepeatingRoles());
        refreshTable.setOutputMarkupId(true);
        return refreshTable;
    }

    private OrderByBorder createRoleDescriptionTableOrder() {
        return new OrderByBorder("tableDescription", "description", roleDataProvider);
    }

    private OrderByBorder createRoleActiveTableOrder() {
        return new OrderByBorder("tableActive", "active", roleDataProvider);
    }

    private RoleDataView createRepeatingRoles() {
        return new RoleDataView("repeatingRoles", roleDataProvider);
    }

    private BubblePanel createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private RoleSearchBoxPanel createSearchBoxPanel() {
        return new RoleSearchBoxPanel(getBoxId(), queryModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.addComponent(refreshTable);
            }
        };
    }

    private AjaxLink<Role> createCreateRoleLink() {
        AjaxLink<Role> link = newCreateRoleLink();
        link.add(createCreateRoleLinkLabel());
        return link;
    }

    private Label createCreateRoleLinkLabel() {
        return new Label(getPageAdminBoxLinkLabelId(), getString("createLink"));
    }

    private AjaxLink<Role> newCreateRoleLink() {
        return new AjaxLink<Role>(getPageAdminBoxLinkId()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                bubblePanel.setContent(createRoleEditPanel());
                bubblePanel.showModal(target);
            }

            private RoleEditPanel createRoleEditPanel() {
                IModel<Role> roleModel = Model.of(roleService.newRoleEntity());
                return new RoleEditPanel(bubblePanel.getContentId(), roleModel) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSave(AjaxRequestTarget target) {
                        rightService.refreshGlobalApplicationRights();
                        bubblePanel.hide(target);
                        info(getString("msg.saved"));
                        target.addComponent(refreshTable);
                        target.addComponent(RolePage.this.getFeedback());
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        bubblePanel.hide(target);
                    }
                };
            }
        };
    }

    private class RoleDataView extends DataView<Role> {
        private static final long serialVersionUID = 1L;

        public RoleDataView(String id, IDataProvider<Role> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<Role> item) {

            item.add(createRoleDescriptionLabel(item));
            item.add(createRoleActiveLabel(item));
            item.add(createEditLink(item));
            item.add(createDeleteLink(item));
            item.add(createAlternatingModifier(item));
        }

        private Label createRoleDescriptionLabel(Item<Role> item) {
            return new Label("description", item.getModelObject().getDescription());
        }

        private Label createRoleActiveLabel(Item<Role> item) {
            return new Label("active", getActiveString(item));
        }

        private String getActiveString(Item<Role> item) {
            return item.getModelObject().getActive() ? getString("status.active") : this.getString("status.inactive");
        }

        private AjaxLink<Role> createDeleteLink(final Item<Role> item) {
            AjaxLink<Role> deleteLink = newDeleteLink(item);
            deleteLink.add(createDeleteLinkImage());
            deleteLink.setOutputMarkupId(true);
            return deleteLink;
        }

        private Image createDeleteLinkImage() {
            return new Image("deleteImage", CommonConstants.REF_DELETE_IMG);
        }

        private AjaxLink<Role> newDeleteLink(final Item<Role> item) {
            return new AjaxLink<Role>("deleteLink") {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    IModel<Role> roleModel = item.getModel();
                    String validationMessage = validateRoleForDeletion(roleModel);
                    if (validationMessage != null) {
                        bubblePanel.showMessage(getMarkupId(), target, validationMessage);
                    } else {
                        bubblePanel.setContent(createConfirmDeletePanel(roleModel));
                        bubblePanel.showModal(target);
                    }
                }

                private String validateRoleForDeletion(IModel<Role> roleModel) {
                    Role role = roleModel.getObject();
                    long numberOfUserInRole = userService.countUserForRole(role);
                    String msg = null;
                    if (isGuestRole(role)) {
                        msg = getString("msg.cannot.delete.guestrole", numberOfUserInRole);
                    } else if (isRegistrationRole(role)) {
                        msg = getString("msg.cannot.delete.reguserrole", numberOfUserInRole);
                    } else if (numberOfUserInRole != 0) {
                        msg = getString("msg.cannot.delete.assigneduser", numberOfUserInRole);
                    }
                    return msg;
                }

                private boolean isGuestRole(Role role) {
                    Integer guestRoleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE);
                    return guestRoleId.equals(role.getId());
                }

                private boolean isRegistrationRole(Role role) {
                    Integer reguserRoleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE);
                    return reguserRoleId.equals(role.getId());
                }

                private String getString(String key, Long numUser) {
                    return new StringResourceModel(key, this, null, new Object[]{numUser}).getString();
                }

                private ConfirmDeletePanel<Role> createConfirmDeletePanel(final IModel<Role> roleModel) {
                    return new ConfirmDeletePanel<Role>(bubblePanel.getContentId(), roleModel, bubblePanel) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onDelete(AjaxRequestTarget target, Form<?> form) {
                            Role role = roleModel.getObject();
                            roleService.delete(role);
                            rightService.refreshGlobalApplicationRights();
                            bubblePanel.hide(target);
                            info(getString("msg.deleted"));
                            target.addComponent(refreshTable);
                            target.addComponent(getFeedback());
                        }

                    };
                }
            };
        }

        private AjaxLink<Role> createEditLink(Item<Role> item) {
            AjaxLink<Role> editLink = newRoleEditLink(item);
            editLink.add(createEditLinkImage());
            return editLink;
        }

        private Image createEditLinkImage() {
            return new Image("editImage", CommonConstants.REF_EDIT_IMG);
        }

        private AjaxLink<Role> newRoleEditLink(final Item<Role> item) {
            return new AjaxLink<Role>("editLink", item.getModel()) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onClick(AjaxRequestTarget target) {
                    bubblePanel.setContent(createRoleEditPanel(item.getModel()));
                    bubblePanel.showModal(target);
                }

                private RoleEditPanel createRoleEditPanel(IModel<Role> roleModel) {
                    return new RoleEditPanel(bubblePanel.getContentId(), roleModel) {
                        private static final long serialVersionUID = 6979098758367103659L;

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

        private AttributeModifier createAlternatingModifier(final Item<Role> item) {
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
