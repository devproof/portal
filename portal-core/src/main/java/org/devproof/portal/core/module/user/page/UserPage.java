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
package org.devproof.portal.core.module.user.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.component.TooltipLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.panel.UserEditPanel;
import org.devproof.portal.core.module.user.panel.UserInfoPanel;
import org.devproof.portal.core.module.user.panel.UserSearchBoxPanel;
import org.devproof.portal.core.module.user.query.UserQuery;
import org.devproof.portal.core.module.user.service.UserService;

import java.text.SimpleDateFormat;

/**
 * @author Carsten Hufe
 */
public class UserPage extends TemplatePage {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "userDataProvider")
    private QueryDataProvider<UserEntity, UserQuery> userDataProvider;
    @SpringBean(name = "userService")
    private UserService userService;
    @SpringBean(name = "displayDateFormat")
    private SimpleDateFormat dateFormat;
    private BubblePanel bubblePanel;
    private WebMarkupContainer userRefreshTableContainer;
    private IModel<UserQuery> queryModel;
    private UserDataView userDataView;

    public UserPage(PageParameters params) {
        super(params);
        this.queryModel = userDataProvider.getSearchQueryModel();
        add(createBubblePanel());
        add(createUserRefreshContainer());
        addFilterBox(createUserSearchBoxPanel());
        addPageAdminBoxLink(createCreateUserLink());
    }

    private AjaxLink<UserEntity> createCreateUserLink() {
        AjaxLink<UserEntity> createLink = newCreateUserLink();
        createLink.add(createCreateUserLinkLabel());
        return createLink;
    }

    private Label createCreateUserLinkLabel() {
        return new Label(getPageAdminBoxLinkLabelId(), getString("createLink"));
    }

    private AjaxLink<UserEntity> newCreateUserLink() {
        return new AjaxLink<UserEntity>(getPageAdminBoxLinkId()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                bubblePanel.setContent(createUserEditPanel());
                bubblePanel.showModal(target);
            }

            private UserEditPanel createUserEditPanel() {
                IModel<UserEntity> userModel = Model.of(userService.newUserEntity());
                return new UserEditPanel(bubblePanel.getContentId(), userModel, true) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onSave(AjaxRequestTarget target) {
                        target.addComponent(userRefreshTableContainer);
                        target.addComponent(UserPage.this.getFeedback());
                        info(getString("msg.saved"));
                        bubblePanel.hide(target);
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        bubblePanel.hide(target);
                    }
                };
            }
        };
    }

    private BubblePanel createBubblePanel() {
        bubblePanel = new BubblePanel("bubblePanel");
        return bubblePanel;
    }

    private WebMarkupContainer createUserRefreshContainer() {
        userRefreshTableContainer = new WebMarkupContainer("refreshTable");
        userRefreshTableContainer.add(createUsernameTableOrder());
        userRefreshTableContainer.add(createFirstnameTableOrder());
        userRefreshTableContainer.add(createLastnameTableOrder());
        userRefreshTableContainer.add(createRoleTableOrder());
        userRefreshTableContainer.add(createRegistrationDateTableOrder());
        userRefreshTableContainer.add(createActiveTableOrder());
        userRefreshTableContainer.add(createUserDataView());
        userRefreshTableContainer.add(createPageNavigatorTop());
        userRefreshTableContainer.add(createPageNavigatorBottom());
        userRefreshTableContainer.setOutputMarkupId(true);
        return userRefreshTableContainer;
    }

    private PagingNavigator createPageNavigatorBottom() {
        return new PagingNavigator("navigatorBottom", userDataView);
    }

    private PagingNavigator createPageNavigatorTop() {
        return new PagingNavigator("navigatorTop", userDataView);
    }

    private UserDataView createUserDataView() {
        userDataView = new UserDataView("tableRow", userDataProvider);
        return userDataView;
    }

    private OrderByBorder createActiveTableOrder() {
        return new OrderByBorder("table_active", "active", userDataProvider);
    }

    private OrderByBorder createRegistrationDateTableOrder() {
        return new OrderByBorder("table_regdate", "registrationDate", userDataProvider);
    }

    private OrderByBorder createRoleTableOrder() {
        return new OrderByBorder("table_role", "role.description", userDataProvider);
    }

    private OrderByBorder createLastnameTableOrder() {
        return new OrderByBorder("table_lastname", "lastname", userDataProvider);
    }

    private OrderByBorder createFirstnameTableOrder() {
        return new OrderByBorder("table_firstname", "firstname", userDataProvider);
    }

    private OrderByBorder createUsernameTableOrder() {
        return new OrderByBorder("table_username", "username", userDataProvider);
    }

    private UserSearchBoxPanel createUserSearchBoxPanel() {
        return new UserSearchBoxPanel("box", queryModel) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                target.addComponent(userRefreshTableContainer);
            }
        };
    }

    private class UserDataView extends DataView<UserEntity> {
        private static final long serialVersionUID = 1L;

        public UserDataView(String id, IDataProvider<UserEntity> dataProvider) {
            super(id, dataProvider);
            setItemsPerPage(50);
        }

        @Override
        protected void populateItem(Item<UserEntity> item) {
            IModel<UserEntity> userModel = item.getModel();
            item.add(createToolTipLabel(userModel));
            item.add(createFirstnameLabel(userModel));
            item.add(createLastnameLabel(userModel));
            item.add(createRoleNameLabel(userModel));
            item.add(createUserRegistrationDateLabel(userModel));
            item.add(createUserActiveLabel(userModel));
            item.add(createAuthorPanel(userModel));
            item.add(createAlternatingModifier(item));
        }

        private Label createLastnameLabel(IModel<UserEntity> userModel) {
            IModel<String> lastnameModel = new PropertyModel<String>(userModel, "lastname");
            Label lastnameLabel = new Label("lastname", lastnameModel);
            lastnameLabel.add(createUnconfirmedAttributeModifier(userModel));
            return lastnameLabel;
        }

        private AttributeModifier createUnconfirmedAttributeModifier(final IModel<UserEntity> userModel) {
            return new AttributeModifier("style", true, new AbstractReadOnlyModel<Object>() {
                private static final long serialVersionUID = -2152809502598433353L;
                @Override
                public Object getObject() {
                    UserEntity user = userModel.getObject();
                    return !user.getConfirmed() ? "text-decoration:line-through;" : "";
                }
            });
        }

        private Label createFirstnameLabel(IModel<UserEntity> userModel) {
            IModel<String> firstnameModel = new PropertyModel<String>(userModel, "firstname");
            Label firstnameLabel = new Label("firstname", firstnameModel);
            firstnameLabel.add(createUnconfirmedAttributeModifier(userModel));
            return firstnameLabel;
        }


        private TooltipLabel createToolTipLabel(IModel<UserEntity> userModel) {
            UserInfoPanel userInfo = createUserInfoPanel(userModel);
            Label usernameLabel = createUsernameLabel(userModel);
            return new TooltipLabel("username", usernameLabel, userInfo);
        }

        private UserInfoPanel createUserInfoPanel(IModel<UserEntity> userModel) {
            return new UserInfoPanel("tooltip", userModel);
        }

        private Label createUsernameLabel(IModel<UserEntity> userModel) {
            IModel<String> usernameModel = new PropertyModel<String>(userModel, "username");
            Label usernameLabel = new Label("label", usernameModel);
            usernameLabel.add(createUnconfirmedAttributeModifier(userModel));
            return usernameLabel;
        }

        private Label createRoleNameLabel(IModel<UserEntity> userModel) {
            IModel<String> roleDescModel = new PropertyModel<String>(userModel, "role.description");
            return new Label("role", roleDescModel);
        }

        private Label createUserRegistrationDateLabel(final IModel<UserEntity> userModel) {
            return new Label("registration", createRegistrationDateModel(userModel));
        }

        private AbstractReadOnlyModel<String> createRegistrationDateModel(final IModel<UserEntity> userModel) {
            return new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = -6429266930696221446L;

                @Override
                public String getObject() {
                    UserEntity user = userModel.getObject();
                    return dateFormat.format(user.getRegistrationDate());
                }
            };
        }

        private Label createUserActiveLabel(IModel<UserEntity> userModel) {
            return new Label("active", createActiveModel(userModel));
        }

        private IModel<String> createActiveModel(final IModel<UserEntity> userModel) {
            return new AbstractReadOnlyModel<String>() {
                private static final long serialVersionUID = 5831214219470331468L;
                @Override
                public String getObject() {
                    UserEntity user = userModel.getObject();
                    return user.getActive() != null ? getString("active." + user.getActive().toString()) : "";
                }
            };
        }

        private AuthorPanel<UserEntity> createAuthorPanel(final IModel<UserEntity> userModel) {
            return new AuthorPanel<UserEntity>("authorButtons", userModel) {
                private static final long serialVersionUID = 1L;

                @Override
                public void onDelete(AjaxRequestTarget target) {
                    userService.delete(userModel.getObject());
                    target.addComponent(userRefreshTableContainer);
                    target.addComponent(getFeedback());
                    info(getString("msg.deleted"));
                }

                @Override
                public void onEdit(AjaxRequestTarget target) {
                    bubblePanel.setContent(createUserEditPanel(userModel));
                    bubblePanel.showModal(target);
                }

                private UserEditPanel createUserEditPanel(final IModel<UserEntity> userModel) {
                    return new UserEditPanel(bubblePanel.getContentId(), userModel, false) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public void onSave(AjaxRequestTarget target) {
                            target.addComponent(userRefreshTableContainer);
                            target.addComponent(getFeedback());
                            info(getString("msg.saved"));
                            bubblePanel.hide(target);
                        }

                        @Override
                        public void onCancel(AjaxRequestTarget target) {
                            bubblePanel.hide(target);
                        }
                    };
                }
            };
        }

        private AttributeModifier createAlternatingModifier(final Item<UserEntity> item) {
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
