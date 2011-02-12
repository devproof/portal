/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.email.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.email.entity.EmailTemplate;
import org.devproof.portal.core.module.email.service.EmailService;

import java.util.Arrays;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class EmailTemplateBasePage extends TemplatePage {
    private static final long serialVersionUID = 1L;
    @SpringBean(name = "emailService")
    private EmailService emailService;

    public EmailTemplateBasePage(PageParameters params) {
        super(params);
    }

    @Override
    protected Component newPageAdminBoxLink(String linkMarkupId, String labelMarkupId) {
        return createCreateEmailTemplateLink(linkMarkupId, labelMarkupId);
    }

    private Link<EmailTemplate> createCreateEmailTemplateLink(String linkMarkupId, String labelMarkupId) {
        Link<EmailTemplate> adminLink = newCreateEmailTemplateLink(linkMarkupId);
        adminLink.add(createCreateEmailTemplateLinkLabel(labelMarkupId));
        return adminLink;
    }

    private Label createCreateEmailTemplateLinkLabel(String labelMarkupId) {
        return new Label(labelMarkupId, getString("createLink"));
    }

    private Link<EmailTemplate> newCreateEmailTemplateLink(String linkMarkupId) {
        return new Link<EmailTemplate>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                EmailTemplate newEmailTemplate = emailService.newEmailTemplateEntity();
                IModel<EmailTemplate> emailTemplateModel = Model.of(newEmailTemplate);
                setResponsePage(new EmailTemplateEditPage(emailTemplateModel));
            }
        };
    }
}
