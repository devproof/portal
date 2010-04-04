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
package org.devproof.portal.core.module.email.page;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.*;
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
	private EmailService emailService;
	@SpringBean(name = "emailTemplateDataProvider")
	private ISortableDataProvider<EmailTemplateEntity> emailTemplateDataProvider;

	public EmailTemplatePage(PageParameters params) {
		super(params);
		add(createSubjectTableOrder());
		add(createModifiedTableOrder());
		add(createEmailTemplateDataView());
	}

	private OrderByBorder createModifiedTableOrder() {
		return new OrderByBorder("table_modified_by", "modifiedBy", emailTemplateDataProvider);
	}

	private OrderByBorder createSubjectTableOrder() {
		return new OrderByBorder("table_subject", "subject", emailTemplateDataProvider);
	}

	private EmailTemplateDataView createEmailTemplateDataView() {
		return new EmailTemplateDataView("tableRow");
	}

	private class EmailTemplateDataView extends DataView<EmailTemplateEntity> {
		private static final long serialVersionUID = 1L;

		public EmailTemplateDataView(final String id) {
			super(id, emailTemplateDataProvider);
		}

		@Override
		protected void populateItem(Item<EmailTemplateEntity> item) {
            item.add(createSubjectLabel(item));
			item.add(createModifiedByLabel(item));
			item.add(createAuthorPanel(item));
			item.add(createAlternatingCssClassModifier(item));
			item.setOutputMarkupId(true);
		}

		private Label createModifiedByLabel(Item<EmailTemplateEntity> item) {
            IModel<String> modifiedByModel = new PropertyModel<String>(item.getModel(), "modifiedBy");
            return new Label("modifiedBy", modifiedByModel);
		}

		private Label createSubjectLabel(Item<EmailTemplateEntity> item) {
            IModel<String> subjectModel = new PropertyModel<String>(item.getModel(), "subject");
            return new Label("subject", subjectModel);
		}

		private AttributeModifier createAlternatingCssClassModifier(Item<EmailTemplateEntity> item) {
			return new AttributeModifier("class", true, createAlternatingModel(item));
		}

		private AbstractReadOnlyModel<String> createAlternatingModel(final Item<EmailTemplateEntity> item) {
			return new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return (item.getIndex() % 2 != 0) ? "even" : "odd";
				}
			};
		}

		private AuthorPanel<EmailTemplateEntity> createAuthorPanel(final Item<EmailTemplateEntity> item) {
			return new AuthorPanel<EmailTemplateEntity>("authorButtons", item.getModel()) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					emailService.delete(getEntityModel().getObject());
					info(EmailTemplatePage.this.getString("msg.deleted"));
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
				}

				@Override
				public void onEdit(AjaxRequestTarget target) {
                    setResponsePage(new EmailTemplateEditPage(item.getModel()));
				}
			};
		}
    }
}
