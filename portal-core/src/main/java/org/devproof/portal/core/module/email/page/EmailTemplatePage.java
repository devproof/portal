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
package org.devproof.portal.core.module.email.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.devproof.portal.core.module.email.service.EmailService;

/**
 * @author Carsten Hufe
 */
public class EmailTemplatePage extends EmailTemplateBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "emailService")
	private transient EmailService emailService;
	@SpringBean(name = "emailTemplateDataProvider")
	private ISortableDataProvider<EmailTemplateEntity> emailTemplateDataProvider;

	public EmailTemplatePage(final PageParameters params) {
		super(params);
		this.add(new OrderByBorder("table_subject", "subject", this.emailTemplateDataProvider));
		this.add(new OrderByBorder("table_modified_by", "modifiedBy", this.emailTemplateDataProvider));
		this.add(new EmailTemplateDataView("tableRow"));
	}

	private class EmailTemplateDataView extends DataView<EmailTemplateEntity> {
		private static final long serialVersionUID = 1L;

		public EmailTemplateDataView(final String id) {
			super(id, EmailTemplatePage.this.emailTemplateDataProvider);
		}

		@Override
		protected void populateItem(final Item<EmailTemplateEntity> item) {
			final EmailTemplateEntity template = item.getModelObject();
			item.setOutputMarkupId(true);
			item.add(new Label("subject", template.getSubject()));
			item.add(new Label("modifiedBy", template.getModifiedBy()));

			item.add(new AuthorPanel<EmailTemplateEntity>("authorButtons", template) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					EmailTemplatePage.this.emailService.delete(getEntity());
					info(EmailTemplatePage.this.getString("msg.deleted"));
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					// Reload because LazyIntialization occur
					EmailTemplateEntity tmp = EmailTemplatePage.this.emailService.findById(template.getId());
					this.setResponsePage(new EmailTemplateEditPage(tmp));
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
	}
}
