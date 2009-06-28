/*
 * Copyright 2009 Carsten Hufe devproof.org
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

	private final ModalWindow modalWindow;
	private final WebMarkupContainer container;
	private boolean authorLinksAdded = false;

	public UserPage(final PageParameters params) {
		super(params);
		UserQuery query = new UserQuery();
		this.userDataProvider.setQueryObject(query);

		this.container = new WebMarkupContainer("refreshTable");
		this.container.setOutputMarkupId(true);
		this.container.add(new OrderByBorder("table_username", "username", this.userDataProvider));
		this.container.add(new OrderByBorder("table_firstname", "firstname", this.userDataProvider));
		this.container.add(new OrderByBorder("table_lastname", "lastname", this.userDataProvider));
		this.container.add(new OrderByBorder("table_role", "role.description", this.userDataProvider));
		this.container.add(new OrderByBorder("table_regdate", "registrationDate", this.userDataProvider));
		this.container.add(new OrderByBorder("table_active", "active", this.userDataProvider));
		this.add(this.container);

		this.modalWindow = new ModalWindow("modalWindow");
		this.modalWindow.setTitle("Portal");
		this.add(this.modalWindow);

		UserDataView dataView = new UserDataView("tableRow", this.userDataProvider, params);
		this.container.add(dataView);

		addFilterBox(new UserSearchBoxPanel("box", query) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target) {
				target.addComponent(UserPage.this.container);
			}

		});
		if (!this.authorLinksAdded) {
			this.authorLinksAdded = true;
			AjaxLink<UserEntity> createLink = new AjaxLink<UserEntity>("adminLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					final UserEditPanel editUserPanel = new UserEditPanel(UserPage.this.modalWindow.getContentId(), new UserEntity(), true) {

						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(final AjaxRequestTarget target) {
							target.addComponent(UserPage.this.container);
							target.addComponent(UserPage.this.getFeedback());
							info(this.getString("msg.saved"));
							UserPage.this.modalWindow.close(target);
						}

					};
					UserPage.this.modalWindow.setInitialHeight(440);
					UserPage.this.modalWindow.setInitialWidth(550);
					UserPage.this.modalWindow.setContent(editUserPanel);
					UserPage.this.modalWindow.show(target);
				}
			};
			createLink.add(new Label("linkName", this.getString("createLink")));
			addPageAdminBoxLink(createLink);

		}
		this.container.add(new PagingNavigator("navigatorTop", dataView));
		this.container.add(new PagingNavigator("navigatorBottom", dataView));

	}

	private class UserDataView extends DataView<UserEntity> {
		private static final long serialVersionUID = 1L;

		public UserDataView(final String id, final IDataProvider<UserEntity> dataProvider, final PageParameters params) {
			super(id, dataProvider);
			setItemsPerPage(50);
		}

		@Override
		protected void populateItem(final Item<UserEntity> item) {
			final UserEntity user = item.getModelObject();
			UserInfoPanel userInfo = new UserInfoPanel("tooltip", user);
			Label tmp = new Label("label", user.getUsername());
			TooltipLabel tooltip = new TooltipLabel("username", tmp, userInfo);
			item.add(tooltip);
			if (!user.getConfirmed()) {
				tmp.add(new SimpleAttributeModifier("style", "text-decoration:line-through;"));
			}
			item.add(tmp = new Label("firstname", user.getFirstname()));
			if (!user.getConfirmed()) {
				tmp.add(new SimpleAttributeModifier("style", "text-decoration:line-through;"));
			}
			item.add(tmp = new Label("lastname", user.getLastname()));
			if (!user.getConfirmed()) {
				tmp.add(new SimpleAttributeModifier("style", "text-decoration:line-through;"));
			}
			item.add(new Label("role", user.getRole().getDescription()));
			item.add(new Label("registration", UserPage.this.dateFormat.format(user.getRegistrationDate())));
			item.add(new Label("active", user.getActive() != null ? this.getString("active." + user.getActive().toString()) : ""));
			item.add(new AuthorPanel<UserEntity>("authorButtons", user) {

				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					UserPage.this.userService.delete(user);
					target.addComponent(UserPage.this.container);
					target.addComponent(getFeedback());
					info(this.getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					final UserEditPanel editUserPanel = new UserEditPanel(UserPage.this.modalWindow.getContentId(), user, false) {

						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(final AjaxRequestTarget target) {
							target.addComponent(UserPage.this.container);
							target.addComponent(getFeedback());
							info(this.getString("msg.saved"));
							UserPage.this.modalWindow.close(target);
						}

					};

					UserPage.this.modalWindow.setContent(editUserPanel);
					UserPage.this.modalWindow.show(target);
				}

			});

			item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 != 0) ? "even" : "odd";
				}
			}));
		}
	};
}
