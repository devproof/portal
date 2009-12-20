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

import java.text.SimpleDateFormat;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.component.TooltipLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.panel.UserEditPanel;
import org.devproof.portal.core.module.user.panel.UserInfoPanel;
import org.devproof.portal.core.module.user.panel.UserSearchBoxPanel;
import org.devproof.portal.core.module.user.query.UserQuery;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class UserPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "userDataProvider")
	private QueryDataProvider<UserEntity> userDataProvider;
	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "dateFormat")
	private SimpleDateFormat dateFormat;
	private PageParameters params;
	private ModalWindow modalWindow;
	private WebMarkupContainer userRefreshTableContainer;
	private UserQuery query = new UserQuery();
	private UserDataView userDataView;

	public UserPage(PageParameters params) {
		super(params);
		this.params = params;
		setQueryToDataProvider();
		add(createModalWindow());
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
		return new Label("linkName", getString("createLink"));
	}

	private AjaxLink<UserEntity> newCreateUserLink() {
		AjaxLink<UserEntity> createLink = new AjaxLink<UserEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				modalWindow.setInitialHeight(440);
				modalWindow.setInitialWidth(550);
				modalWindow.setContent(createUserEditPanel());
				modalWindow.show(target);
			}

			private UserEditPanel createUserEditPanel() {
				return new UserEditPanel(modalWindow.getContentId(), new UserEntity(), true) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSave(AjaxRequestTarget target) {
						target.addComponent(userRefreshTableContainer);
						target.addComponent(UserPage.this.getFeedback());
						info(getString("msg.saved"));
						modalWindow.close(target);
					}
				};
			}
		};
		return createLink;
	}

	private ModalWindow createModalWindow() {
		modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		return modalWindow;
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
		userDataView = new UserDataView("tableRow", userDataProvider, params);
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
		return new UserSearchBoxPanel("box", query) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				target.addComponent(userRefreshTableContainer);
			}
		};
	}

	private void setQueryToDataProvider() {
		userDataProvider.setQueryObject(query);
	}

	private class UserDataView extends DataView<UserEntity> {
		private static final long serialVersionUID = 1L;

		public UserDataView(String id, IDataProvider<UserEntity> dataProvider, PageParameters params) {
			super(id, dataProvider);
			setItemsPerPage(50);
		}

		@Override
		protected void populateItem(Item<UserEntity> item) {
			item.add(createToolTipLabel(item));
			item.add(createFirstnameLabel(item));
			item.add(createLastnameLabel(item));
			item.add(createRoleNameLabel(item));
			item.add(createUserRegistrationDateLabel(item));
			item.add(createUserActiveLabel(item));
			item.add(createAuthorPanel(item));
			item.add(createAlternatingModifier(item));
		}

		private Label createLastnameLabel(Item<UserEntity> item) {
			UserEntity user = item.getModelObject();
			Label lastnameLabel = new Label("lastname", user.getLastname());
			if (!user.getConfirmed()) {
				lastnameLabel.add(new SimpleAttributeModifier("style", "text-decoration:line-through;"));
			}
			return lastnameLabel;
		}

		private Label createFirstnameLabel(Item<UserEntity> item) {
			UserEntity user = item.getModelObject();
			Label firstnameLabel = new Label("firstname", user.getFirstname());
			if (!user.getConfirmed()) {
				firstnameLabel.add(new SimpleAttributeModifier("style", "text-decoration:line-through;"));
			}
			return firstnameLabel;
		}

		private TooltipLabel createToolTipLabel(Item<UserEntity> item) {
			UserEntity user = item.getModelObject();
			UserInfoPanel userInfo = createUserInfoPanel(user);
			Label usernameLabel = createUsernameLabel(user);
			return new TooltipLabel("username", usernameLabel, userInfo);
		}

		private UserInfoPanel createUserInfoPanel(UserEntity user) {
			UserInfoPanel userInfo = new UserInfoPanel("tooltip", user);
			return userInfo;
		}

		private Label createUsernameLabel(UserEntity user) {
			Label usernameLabel = new Label("label", user.getUsername());
			if (!user.getConfirmed()) {
				usernameLabel.add(new SimpleAttributeModifier("style", "text-decoration:line-through;"));
			}
			return usernameLabel;
		}

		private Label createRoleNameLabel(Item<UserEntity> item) {
			return new Label("role", item.getModelObject().getRole().getDescription());
		}

		private Label createUserRegistrationDateLabel(Item<UserEntity> item) {
			return new Label("registration", dateFormat.format(item.getModelObject().getRegistrationDate()));
		}

		private Label createUserActiveLabel(Item<UserEntity> item) {
			return new Label("active", getActiveString(item));
		}

		private String getActiveString(Item<UserEntity> item) {
			return item.getModelObject().getActive() != null ? getString("active."
					+ item.getModelObject().getActive().toString()) : "";
		}

		private AuthorPanel<UserEntity> createAuthorPanel(final Item<UserEntity> item) {
			return new AuthorPanel<UserEntity>("authorButtons", item.getModelObject()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					userService.delete(item.getModelObject());
					target.addComponent(userRefreshTableContainer);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(AjaxRequestTarget target) {
					modalWindow.setContent(createUserEditPanel(item));
					modalWindow.show(target);
				}

				private UserEditPanel createUserEditPanel(final Item<UserEntity> item) {
					UserEditPanel editUserPanel = new UserEditPanel(modalWindow.getContentId(), item.getModelObject(),
							false) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(AjaxRequestTarget target) {
							target.addComponent(userRefreshTableContainer);
							target.addComponent(getFeedback());
							info(getString("msg.saved"));
							modalWindow.close(target);
						}
					};
					return editUserPanel;
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
	};
}
