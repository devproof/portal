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

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.devproof.portal.core.module.email.service.EmailService;

/**
 * @author Carsten Hufe
 */
public class EmailTemplateBasePage extends TemplatePage {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "emailService")
	private EmailService emailService;

	public EmailTemplateBasePage(PageParameters params) {
		super(params);
		addPageAdminBoxLink(createCreateEmailTemplateLink());
	}

	private Link<EmailTemplateEntity> createCreateEmailTemplateLink() {
		Link<EmailTemplateEntity> adminLink = newCreateEmailTemplateLink();
		adminLink.add(createCreateEmailTemplateLinkLabel());
		return adminLink;
	}

	private Label createCreateEmailTemplateLinkLabel() {
		return new Label("linkName", getString("createLink"));
	}

	private Link<EmailTemplateEntity> newCreateEmailTemplateLink() {
		Link<EmailTemplateEntity> adminLink = new Link<EmailTemplateEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				EmailTemplateEntity newEmailTemplate = emailService.newEmailTemplateEntity();
				IModel<EmailTemplateEntity> emailTemplateModel = Model.of(newEmailTemplate);
				setResponsePage(new EmailTemplateEditPage(emailTemplateModel));
			}
		};
		return adminLink;
	}
}
