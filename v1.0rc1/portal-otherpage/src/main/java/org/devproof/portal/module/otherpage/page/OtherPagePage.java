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
package org.devproof.portal.module.otherpage.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.ExternalImage;
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

	public OtherPagePage(final PageParameters params) {
		super(params);
		this.add(new OrderByBorder("table_content_id", "subject", this.otherPageDataProvider));
		this.add(new OrderByBorder("table_modified_by", "modifiedBy", this.otherPageDataProvider));
		this.add(new OtherPageDataView("tableRow"));
	}

	private class OtherPageDataView extends DataView<OtherPageEntity> {
		private static final long serialVersionUID = 1L;

		public OtherPageDataView(final String id) {
			super(id, OtherPagePage.this.otherPageDataProvider);
		}

		@Override
		protected void populateItem(final Item<OtherPageEntity> item) {
			final OtherPageEntity otherPage = item.getModelObject();

			item.add(new Label("contentId", otherPage.getContentId()));
			item.add(new Label("modifiedBy", otherPage.getModifiedBy()));
			BookmarkablePageLink<OtherPageViewPage> viewLink = new BookmarkablePageLink<OtherPageViewPage>("viewLink", OtherPageViewPage.class);
			viewLink.setParameter("0", otherPage.getContentId());
			viewLink.add(new ExternalImage("viewImage", CommonConstants.REF_VIEW_IMG).setEscapeModelStrings(false));
			item.add(viewLink);
			item.setOutputMarkupId(true);
			item.add(new AuthorPanel<OtherPageEntity>("authorButtons", otherPage) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					OtherPagePage.this.otherPageService.delete(getEntity());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(OtherPagePage.this.getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					// Reload because LazyIntialization occur
					OtherPageEntity tmp = OtherPagePage.this.otherPageService.findById(otherPage.getId());
					this.setResponsePage(new OtherPageEditPage(tmp));

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
	}
}
