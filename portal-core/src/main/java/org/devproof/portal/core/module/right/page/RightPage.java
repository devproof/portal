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
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
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
	private QueryDataProvider<RightEntity> rightDataProvider;
	@SpringBean(name = "rightService")
	private RightService rightService;
	private WebMarkupContainer refreshTable;
	private ModalWindow modalWindow;
	private RightQuery query = new RightQuery();
	private PageParameters params;

	public RightPage(PageParameters params) {
		super(params);
		this.params = params;
		setQueryToDataProvider();
		add(createRightRefreshTableContainer());
		add(createModalWindow());
		addPageAdminBoxLink(createCreateRightLink());
		addFilterBox(createRightSearchBoxPanel());
	}

	private RightSearchBoxPanel createRightSearchBoxPanel() {
		return new RightSearchBoxPanel("box", query) {
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
		return new RightDataView("tableRow", rightDataProvider, params);
	}

	private OrderByBorder createRightDescriptionTableOrder() {
		return new OrderByBorder("table_description", "description", rightDataProvider);
	}

	private OrderByBorder createRightNameTableOrder() {
		return new OrderByBorder("table_right", "right", rightDataProvider);
	}

	private ModalWindow createModalWindow() {
		modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		return modalWindow;
	}

	private void setQueryToDataProvider() {
		rightDataProvider.setQueryObject(query);
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
				modalWindow.setInitialHeight(440);
				modalWindow.setInitialWidth(620);
				modalWindow.setContent(createRightEditPanel());
				modalWindow.show(target);
			}

			private RightEditPanel createRightEditPanel() {
				IModel<RightEntity> rightModel = Model.of(rightService.newRightEntity());
				return new RightEditPanel(modalWindow.getContentId(), rightModel, true) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSave(AjaxRequestTarget target) {
						target.addComponent(refreshTable);
						target.addComponent(RightPage.this.getFeedback());
						rightService.refreshGlobalApplicationRights();
						info(getString("msg.saved"));
						modalWindow.close(target);
					}
				};
			}
		};
	}

	private class RightDataView extends DataView<RightEntity> {
		private static final long serialVersionUID = 1L;

		public RightDataView(String id, IDataProvider<RightEntity> dataProvider, PageParameters params) {
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

		private AuthorPanel<RightEntity> createAuthorPanel(Item<RightEntity> item) {
			final RightEntity right = item.getModelObject();
			return new AuthorPanel<RightEntity>("authorButtons", right) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					try {
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
					modalWindow.setInitialHeight(440);
					modalWindow.setInitialWidth(620);
					modalWindow.setContent(createRightEditPanel(right));
					modalWindow.show(target);
				}

				private RightEditPanel createRightEditPanel(final RightEntity right) {
					RightEntity refreshedRight = rightService.findById(right.getRight());
					return newRightEditPanel(refreshedRight);
				}

				private RightEditPanel newRightEditPanel(RightEntity refreshedRight) {
					IModel<RightEntity> rightModel = Model.of(refreshedRight);
					return new RightEditPanel(modalWindow.getContentId(), rightModel, false) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(AjaxRequestTarget target) {
							rightService.refreshGlobalApplicationRights();
							target.addComponent(refreshTable);
							target.addComponent(getFeedback());
							info(getString("msg.saved"));
							modalWindow.close(target);
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
	};

}
