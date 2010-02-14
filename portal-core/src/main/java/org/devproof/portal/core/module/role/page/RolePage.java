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
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.core.module.common.panel.ConfirmDeletePanel;
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
	private RoleQuery query = new RoleQuery();
	private WebMarkupContainer refreshTable;
	private BubblePanel bubblePanel;
	private PageParameters params;

	public RolePage(PageParameters params) {
		super(params);
		this.params = params;
		setQueryToDataProvider();
		add(createRoleTableRefreshContainer());
		add(createBubblePanel());
		addPageAdminBoxLink(createCreateRoleLink());
		addFilterBox(createSearchBoxPanel());
	}

	private WebMarkupContainer createRoleTableRefreshContainer() {
		refreshTable = new WebMarkupContainer("refreshTable");
		refreshTable.add(createRoleDescriptionTableOrder());
		refreshTable.add(createRoleActiveTableOrder());
		refreshTable.add(createRoleDataView());
		refreshTable.setOutputMarkupId(true);
		return refreshTable;
	}

	private OrderByBorder createRoleDescriptionTableOrder() {
		return new OrderByBorder("tableDescription", "description", roleDataProvider);
	}

	private OrderByBorder createRoleActiveTableOrder() {
		return new OrderByBorder("tableActive", "active", roleDataProvider);
	}

	private RoleDataView createRoleDataView() {
		return new RoleDataView("tableRow", roleDataProvider, params);
	}

	private BubblePanel createBubblePanel() {
		bubblePanel = new BubblePanel("bubblePanel");
		return bubblePanel;
	}

	private RoleSearchBoxPanel createSearchBoxPanel() {
		return new RoleSearchBoxPanel("box", query) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				target.addComponent(refreshTable);
			}
		};
	}

	private void setQueryToDataProvider() {
		roleDataProvider.setQueryObject(query);
	}

	private AjaxLink<RoleEntity> createCreateRoleLink() {
		AjaxLink<RoleEntity> link = newCreateRoleLink();
		link.add(createCreateRoleLinkLabel());
		return link;
	}

	private Label createCreateRoleLinkLabel() {
		return new Label("linkName", getString("createLink"));
	}

	private AjaxLink<RoleEntity> newCreateRoleLink() {
		return new AjaxLink<RoleEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				bubblePanel.setContent(createRoleEditPanel());
				bubblePanel.showModal(target);
			}

			private RoleEditPanel createRoleEditPanel() {
				IModel<RoleEntity> roleModel = Model.of(roleService.newRoleEntity());
				return new RoleEditPanel(bubblePanel.getContentId(), roleModel, true) {
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

	private class RoleDataView extends DataView<RoleEntity> {
		private static final long serialVersionUID = 1L;

		public RoleDataView(String id, IDataProvider<RoleEntity> dataProvider, PageParameters params) {
			super(id, dataProvider);
		}

		@Override
		protected void populateItem(Item<RoleEntity> item) {

			item.add(createRoleDescriptionLabel(item));
			item.add(createRoleActiveLabel(item));
			item.add(createEditLink(item));
			item.add(createDeleteLink(item));
			item.add(createAlternatingModifier(item));
		}

		private Label createRoleDescriptionLabel(Item<RoleEntity> item) {
			return new Label("description", item.getModelObject().getDescription());
		}

		private Label createRoleActiveLabel(Item<RoleEntity> item) {
			return new Label("active", getActiveString(item));
		}

		private String getActiveString(Item<RoleEntity> item) {
			return item.getModelObject().getActive() ? getString("status.active") : this.getString("status.inactive");
		}

		private AjaxLink<RoleEntity> createDeleteLink(final Item<RoleEntity> item) {
			AjaxLink<RoleEntity> deleteLink = newDeleteLink(item);
			deleteLink.add(createDeleteLinkImage());
			deleteLink.setOutputMarkupId(true);
			return deleteLink;
		}

		private Image createDeleteLinkImage() {
			return new Image("deleteImage", CommonConstants.REF_DELETE_IMG);
		}

		private AjaxLink<RoleEntity> newDeleteLink(final Item<RoleEntity> item) {
			return new AjaxLink<RoleEntity>("deleteLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					RoleEntity role = item.getModelObject();
					String validationMessage = validateRoleForDeletion(role);
					if (validationMessage != null) {
						bubblePanel.showMessage(getMarkupId(), target, validationMessage);
					} else {
						bubblePanel.setContent(createConfirmDeletePanel(role));
						bubblePanel.showModal(target);
					}
				}

				private String validateRoleForDeletion(RoleEntity role) {
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

				private boolean isGuestRole(RoleEntity role) {
					Integer guestRoleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_GUEST_ROLE);
					return guestRoleId.equals(role.getId());
				}

				private boolean isRegistrationRole(RoleEntity role) {
					Integer reguserRoleId = configurationService.findAsInteger(RoleConstants.CONF_DEFAULT_REGUSER_ROLE);
					return reguserRoleId.equals(role.getId());
				}

				private String getString(String key, Long numUser) {
					return new StringResourceModel(key, this, null, new Object[] { numUser }).getString();
				}

				private ConfirmDeletePanel<RoleEntity> createConfirmDeletePanel(final RoleEntity role) {
					return new ConfirmDeletePanel<RoleEntity>(bubblePanel.getContentId(), role, bubblePanel) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onDelete(AjaxRequestTarget target, Form<?> form) {
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

		private AjaxLink<RoleEntity> createEditLink(Item<RoleEntity> item) {
			AjaxLink<RoleEntity> editLink = newRoleEditLink(item);
			editLink.add(createEditLinkImage());
			return editLink;
		}

		private Image createEditLinkImage() {
			return new Image("editImage", CommonConstants.REF_EDIT_IMG);
		}

		private AjaxLink<RoleEntity> newRoleEditLink(Item<RoleEntity> item) {
			final RoleEntity role = item.getModelObject();
			return new AjaxLink<RoleEntity>("editLink", item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					bubblePanel.setContent(createRoleEditPanel(role));
					bubblePanel.showModal(target);
				}

				private RoleEditPanel createRoleEditPanel(final RoleEntity role) {
					RoleEntity refreshedRole = roleService.findById(role.getId());
					return newRoleEditPanel(refreshedRole);
				}

				private RoleEditPanel newRoleEditPanel(RoleEntity refreshedRole) {
					IModel<RoleEntity> roleModel = Model.of(refreshedRole);
					return new RoleEditPanel(bubblePanel.getContentId(), roleModel, false) {
						private static final long serialVersionUID = 1L;

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

		private AttributeModifier createAlternatingModifier(final Item<RoleEntity> item) {
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
