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
package org.devproof.portal.core.module.box.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.box.panel.BoxEditPanel;
import org.devproof.portal.core.module.box.registry.BoxRegistry;
import org.devproof.portal.core.module.box.service.BoxService;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.AuthorPanel;

/**
 * @author Carsten Hufe
 */
public class BoxPage extends TemplatePage {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "boxDataProvider")
	private SortableDataProvider<BoxEntity> boxDataProvider;
	@SpringBean(name = "boxService")
	private BoxService boxService;
	@SpringBean(name = "boxRegistry")
	private BoxRegistry boxRegistry;
	private ModalWindow modalWindow;
	private WebMarkupContainer boxDataViewWithRefreshContainer;

	public BoxPage(PageParameters params) {
		super(params);
		add(boxDataViewWithRefreshContainer = createBoxDataViewWithRefreshContainer());
		add(modalWindow = createModalWindow());
		addPageAdminBoxLink(createAddLink());
	}

	private BoxDataView createBoxDataView() {
		return new BoxDataView("tableRow");
	}

	private ModalWindow createModalWindow() {
		ModalWindow modalWindow = new ModalWindow("modalWindow");
		modalWindow.setTitle("Portal");
		return modalWindow;
	}

	private AjaxLink<BoxEntity> createAddLink() {
		AjaxLink<BoxEntity> createLink = newAddLink();
		createLink.add(new Label("linkName", getString("createLink")));
		return createLink;
	}

	private AjaxLink<BoxEntity> newAddLink() {
		return new AjaxLink<BoxEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(final AjaxRequestTarget target) {
				BoxEditPanel boxEditPanel = new BoxEditPanel(modalWindow.getContentId(), boxService.newBoxEntity()) {

					private static final long serialVersionUID = 1L;

					@Override
					public void onSave(AjaxRequestTarget target) {
						target.addComponent(boxDataViewWithRefreshContainer);
						target.addComponent(BoxPage.this.getFeedback());
						info(getString("msg.saved"));
						modalWindow.close(target);
					}

				};
				modalWindow.setInitialHeight(280);
				modalWindow.setInitialWidth(550);
				modalWindow.setContent(boxEditPanel);
				modalWindow.show(target);
			}
		};
	}

	private WebMarkupContainer createBoxDataViewWithRefreshContainer() {
		WebMarkupContainer refreshContainer = new WebMarkupContainer("refreshTable");
		refreshContainer.setOutputMarkupId(true);
		refreshContainer.add(createBoxDataView());
		return refreshContainer;
	}

	private class BoxDataView extends DataView<BoxEntity> {
		private static final long serialVersionUID = 1L;

		public BoxDataView(String id) {
			super(id, boxDataProvider);
		}

		@Override
		protected void populateItem(Item<BoxEntity> item) {
			BoxEntity box = item.getModelObject();
			item.add(createSortLabel(box));
			item.add(createTypeLabel(box));
			item.add(createTitleLabel(box));
			item.add(createAuthorPanel(box));
			item.add(createMoveUpLink(box));
			item.add(createMoveDownLink(box));
			item.add(createClassEvenOddModifier(item));
		}

		private AttributeModifier createClassEvenOddModifier(final Item<BoxEntity> item) {
			return new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 != 0) ? "even" : "odd";
				}
			});
		}

		private MarkupContainer createMoveDownLink(BoxEntity box) {
			AjaxLink<BoxEntity> moveDownLink = newMoveDownLink(box);
			moveDownLink.add(new Image("downImage", CommonConstants.REF_DOWN_IMG));
			return moveDownLink;
		}

		private AjaxLink<BoxEntity> newMoveDownLink(final BoxEntity box) {
			return new AjaxLink<BoxEntity>("downLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					boxService.moveDown(box);
					target.addComponent(boxDataViewWithRefreshContainer);
				}
			};
		}

		private MarkupContainer createMoveUpLink(BoxEntity box) {
			AjaxLink<BoxEntity> moveUpLink = newMoveUpLink(box);
			moveUpLink.add(new Image("upImage", CommonConstants.REF_UP_IMG));
			return moveUpLink;
		}

		private AjaxLink<BoxEntity> newMoveUpLink(final BoxEntity box) {
			return new AjaxLink<BoxEntity>("upLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					boxService.moveUp(box);
					target.addComponent(boxDataViewWithRefreshContainer);
				}
			};
		}

		private AuthorPanel<BoxEntity> createAuthorPanel(final BoxEntity box) {
			return new AuthorPanel<BoxEntity>("authorButtons", box) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					boxService.delete(box);
					target.addComponent(boxDataViewWithRefreshContainer);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					BoxEditPanel editUserPanel = new BoxEditPanel(modalWindow.getContentId(), box) {

						private static final long serialVersionUID = 1L;

						@Override
						public void onSave(AjaxRequestTarget target) {
							target.addComponent(boxDataViewWithRefreshContainer);
							target.addComponent(getFeedback());
							info(getString("msg.saved"));
							modalWindow.close(target);
						}

					};

					modalWindow.setContent(editUserPanel);
					modalWindow.show(target);
				}

			};
		}

		private Label createTitleLabel(BoxEntity box) {
			return new Label("title", box.getTitle());
		}

		private Label createTypeLabel(BoxEntity box) {
			String name = boxRegistry.getNameBySimpleClassName(box.getBoxType());
			return new Label("type", name);
		}

		private Label createSortLabel(BoxEntity box) {
			return new Label("sort", Integer.toString(box.getSort()));
		}
	};
}
