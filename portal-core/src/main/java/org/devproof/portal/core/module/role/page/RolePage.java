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
package org.devproof.portal.core.module.role.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.ConfirmDeletePanel;
import org.devproof.portal.core.module.common.panel.InfoMessagePanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.core.module.role.RoleConstants;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.role.panel.RoleEditPanel;
import org.devproof.portal.core.module.role.panel.RoleSearchBoxPanel;
import org.devproof.portal.core.module.role.query.RoleQuery;
import org.devproof.portal.core.module.role.service.RoleService;
import org.devproof.portal.core.module.user.service.UserService;

/**
 * @author Carsten Hufe
 */
public class RolePage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "roleDataProvider")
	private QueryDataProvider<RoleEntity> roleDataProvider;
	@SpringBean(name = "roleService")
	private RoleService roleService;
	@SpringBean(name = "userService")
	private UserService userService;
	@SpringBean(name = "rightService")
	private RightService rightService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private final WebMarkupContainer container;
	private final ModalWindow modalWindow;

	public RolePage(final PageParameters params) {
		super(params);
		addPageAdminBoxLink(new AjaxLink<RoleEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				final RoleEditPanel editRolePanel = new RoleEditPanel(modalWindow.getContentId(), roleService
						.newRoleEntity(), true) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onSave(final AjaxRequestTarget target) {
						target.addComponent(container);
						target.addComponent(RolePage.this.getFeedback());
						rightService.refreshGlobalApplicationRights();
						info(getString("msg.saved"));
						modalWindow.close(target);
					}

				};
				modalWindow.setInitialHeight(440);
				modalWindow.setInitialWidth(620);
				modalWindow.setContent(editRolePanel);
				modalWindow.show(target);
			}
		}.add(new Label("linkName", getString("createLink"))));

		RoleQuery query = new RoleQuery();
		roleDataProvider.setQueryObject(query);

		container = new WebMarkupContainer("refreshTable");
		container.setOutputMarkupId(true);
		container.add(new OrderByBorder("tableDescription", "description", roleDataProvider));
		container.add(new OrderByBorder("tableActive", "active", roleDataProvider));
		add(container);

		modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		add(modalWindow);

		final RoleDataView dataView = new RoleDataView("tableRow", roleDataProvider, params);
		container.add(dataView);

		addFilterBox(new RoleSearchBoxPanel("box", query) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target) {
				target.addComponent(container);
			}

		});
	}

	private class RoleDataView extends DataView<RoleEntity> {
		private static final long serialVersionUID = 1L;

		public RoleDataView(final String id, final IDataProvider<RoleEntity> dataProvider, final PageParameters params) {
			super(id, dataProvider);
		}

		@Override
		protected void populateItem(final Item<RoleEntity> item) {
			final RoleEntity role = item.getModelObject();

			item.add(new Label("description", role.getDescription()));
			item.add(new Label("active", role.getActive() ? getString("status.active") : this
					.getString("status.inactive")));

			item.add(new AjaxLink<RoleEntity>("editLink", Model.of(role)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					RoleEntity refreshedRole = roleService.findById(role.getId());
					final RoleEditPanel editRolePanel = new RoleEditPanel(modalWindow.getContentId(), refreshedRole,
							false) {

						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(final AjaxRequestTarget target) {
							target.addComponent(container);
							target.addComponent(getFeedback());
							rightService.refreshGlobalApplicationRights();
							info(getString("msg.saved"));
							modalWindow.close(target);
						}

					};
					modalWindow.setInitialHeight(440);
					modalWindow.setInitialWidth(620);
					modalWindow.setContent(editRolePanel);
					modalWindow.show(target);
				}
			}.add(new Image("editImage", CommonConstants.REF_EDIT_IMG)));

			item.add(new AjaxLink<RoleEntity>("deleteLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					long numUser = userService.countUserForRole(role);
					Integer guestRoleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE);
					Integer reguserRoleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE);
					if (guestRoleId.equals(role.getId())) {
						String msg = new StringResourceModel("msg.cannot.delete.guestrole", this, null,
								new Object[] { numUser }).getString();
						final InfoMessagePanel infoMessagePanel = new InfoMessagePanel(modalWindow.getContentId(), msg,
								modalWindow);
						modalWindow.setContent(infoMessagePanel);
					} else if (reguserRoleId.equals(role.getId())) {
						String msg = new StringResourceModel("msg.cannot.delete.reguserrole", this, null,
								new Object[] { numUser }).getString();
						final InfoMessagePanel infoMessagePanel = new InfoMessagePanel(modalWindow.getContentId(), msg,
								modalWindow);
						modalWindow.setContent(infoMessagePanel);
					} else if (numUser != 0) {
						String msg = new StringResourceModel("msg.cannot.delete.assigneduser", this, null,
								new Object[] { numUser }).getString();
						final InfoMessagePanel infoMessagePanel = new InfoMessagePanel(modalWindow.getContentId(), msg,
								modalWindow);
						modalWindow.setContent(infoMessagePanel);
					} else {
						final ConfirmDeletePanel<RoleEntity> confirmDeletePanel = new ConfirmDeletePanel<RoleEntity>(
								modalWindow.getContentId(), role, modalWindow) {

							private static final long serialVersionUID = 1L;

							@Override
							public void onDelete(final AjaxRequestTarget target, final Form<?> form) {
								roleService.delete(role);
								target.addComponent(container);
								target.addComponent(getFeedback());
								rightService.refreshGlobalApplicationRights();
								info(getString("msg.deleted"));
								modalWindow.close(target);
							}

						};
						modalWindow.setContent(confirmDeletePanel);
					}
					modalWindow.show(target);
				}
			}.add(new Image("deleteImage", CommonConstants.REF_DELETE_IMG)));
			//            
			item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 != 0) ? "even" : "odd";
				}
			}));
		}
	}
}
