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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.devproof.portal.module.otherpage.service.OtherPageService;

/**
 * @author Carsten Hufe
 */
public class OtherPagePage extends OtherPageBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "otherPageService")
	private OtherPageService otherPageService;
	@SpringBean(name = "otherPageDataProvider")
	private ISortableDataProvider<OtherPageEntity> otherPageDataProvider;

	public OtherPagePage(PageParameters params) {
		super(params);
		add(createContentIdOrderHeader());
		add(createModifiedByOrderHeader());
		add(createOtherPageDataView());
	}

	private OrderByBorder createContentIdOrderHeader() {
		return new OrderByBorder("table_content_id", "subject", otherPageDataProvider);
	}

	private OrderByBorder createModifiedByOrderHeader() {
		return new OrderByBorder("table_modified_by", "modifiedBy", otherPageDataProvider);
	}

	private OtherPageDataView createOtherPageDataView() {
		return new OtherPageDataView("tableRow");
	}

	private class OtherPageDataView extends DataView<OtherPageEntity> {
		private static final long serialVersionUID = 1L;

		public OtherPageDataView(String id) {
			super(id, otherPageDataProvider);
		}

		@Override
		protected void populateItem(Item<OtherPageEntity> item) {
			OtherPageEntity otherPage = item.getModelObject();
			item.add(createContentIdLabel(otherPage));
			item.add(createModifiedByLabel(otherPage));
			item.add(createViewLink(otherPage));
			item.add(createAuthorPanel(item));
			item.add(createEvenOddModifier(item));
			item.setOutputMarkupId(true);
		}

		private BookmarkablePageLink<OtherPageViewPage> createViewLink(OtherPageEntity otherPage) {
			BookmarkablePageLink<OtherPageViewPage> viewLink = new BookmarkablePageLink<OtherPageViewPage>("viewLink",
					OtherPageViewPage.class);
			viewLink.add(createViewLinkImage());
			viewLink.setParameter("0", otherPage.getContentId());
			return viewLink;
		}

		private Image createViewLinkImage() {
			Image viewImage = new Image("viewImage", CommonConstants.REF_VIEW_IMG);
			viewImage.setEscapeModelStrings(false);
			return viewImage;
		}

		private AttributeModifier createEvenOddModifier(final Item<OtherPageEntity> item) {
			return new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 != 0) ? "even" : "odd";
				}
			});
		}

		private AuthorPanel<OtherPageEntity> createAuthorPanel(final Item<OtherPageEntity> item) {
			return new AuthorPanel<OtherPageEntity>("authorButtons", item.getModelObject()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					otherPageService.delete(getEntity());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(OtherPagePage.this.getString("msg.deleted"));
				}

				@Override
				public void onEdit(AjaxRequestTarget target) {
					// Reload because LazyIntialization occur
					OtherPageEntity tmp = otherPageService.findById(getEntity().getId());
					setResponsePage(new OtherPageEditPage(tmp));
				}
			};
		}

		private Label createModifiedByLabel(OtherPageEntity otherPage) {
			return new Label("modifiedBy", otherPage.getModifiedBy());
		}

		private Label createContentIdLabel(OtherPageEntity otherPage) {
			return new Label("contentId", otherPage.getContentId());
		}
	}
}
