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
import org.apache.wicket.spring.injection.annot.SpringBean;
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

	private final WebMarkupContainer container;
	private final ModalWindow modalWindow;

	public RightPage(final PageParameters params) {
		super(params);

		addPageAdminBoxLink(new AjaxLink<Object>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				final RightEditPanel editRightPanel = new RightEditPanel(RightPage.this.modalWindow.getContentId(), RightPage.this.rightService.newRightEntity(), true) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSave(final AjaxRequestTarget target) {
						target.addComponent(RightPage.this.container);
						target.addComponent(RightPage.this.getFeedback());
						RightPage.this.rightService.refreshGlobalApplicationRights();
						info(this.getString("msg.saved"));
						RightPage.this.modalWindow.close(target);
					}

				};
				RightPage.this.modalWindow.setInitialHeight(440);
				RightPage.this.modalWindow.setInitialWidth(620);
				RightPage.this.modalWindow.setContent(editRightPanel);
				RightPage.this.modalWindow.show(target);
			}
		}.add(new Label("linkName", this.getString("createLink"))));
		RightQuery query = new RightQuery();
		this.rightDataProvider.setQueryObject(query);

		this.container = new WebMarkupContainer("refreshTable");
		this.container.setOutputMarkupId(true);
		this.add(this.container);

		this.container.add(new OrderByBorder("table_right", "right", this.rightDataProvider));
		this.container.add(new OrderByBorder("table_description", "description", this.rightDataProvider));

		this.modalWindow = new ModalWindow("modalWindow");
		this.modalWindow.setTitle("Portal");
		this.add(this.modalWindow);

		final RightDataView dataView = new RightDataView("tableRow", this.rightDataProvider, params);
		this.container.add(dataView);

		addFilterBox(new RightSearchBoxPanel("box", query) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(final AjaxRequestTarget target) {
				target.addComponent(RightPage.this.container);
			}

		});
	}

	private class RightDataView extends DataView<RightEntity> {
		private static final long serialVersionUID = 1L;

		public RightDataView(final String id, final IDataProvider<RightEntity> dataProvider, final PageParameters params) {
			super(id, dataProvider);
		}

		@Override
		protected void populateItem(final Item<RightEntity> item) {
			final RightEntity right = item.getModelObject();

			item.add(new Label("right", right.getRight()));
			item.add(new Label("description", right.getDescription()));
			item.add(new AuthorPanel<RightEntity>("authorButtons", right) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					RightPage.this.rightService.delete(right);
					target.addComponent(RightPage.this.container);
					target.addComponent(getFeedback());
					RightPage.this.rightService.refreshGlobalApplicationRights();
					info(this.getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					RightEntity refreshedRight = RightPage.this.rightService.findById(right.getRight());
					final RightEditPanel editRightPanel = new RightEditPanel(RightPage.this.modalWindow.getContentId(), refreshedRight, false) {

						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(final AjaxRequestTarget target) {
							target.addComponent(RightPage.this.container);
							target.addComponent(getFeedback());
							RightPage.this.rightService.refreshGlobalApplicationRights();
							info(this.getString("msg.saved"));
							RightPage.this.modalWindow.close(target);
						}

					};
					RightPage.this.modalWindow.setInitialHeight(440);
					RightPage.this.modalWindow.setInitialWidth(620);
					RightPage.this.modalWindow.setContent(editRightPanel);
					RightPage.this.modalWindow.show(target);

				}

			});

			item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 == 1) ? "even" : "odd";
				}
			}));
		}
	};

}
