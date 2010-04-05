/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.email.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.component.richtext.BasicRichTextArea;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.devproof.portal.core.module.email.service.EmailService;

/**
 * @author Carsten Hufe
 */
public class EmailTemplateEditPage extends EmailTemplateBasePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "emailService")
    private EmailService emailService;
    private IModel<EmailTemplateEntity> emailTemplateModel;

    public EmailTemplateEditPage(IModel<EmailTemplateEntity> emailTemplateModel) {
        super(new PageParameters());
        this.emailTemplateModel = emailTemplateModel;
        add(createEditEmailTemplateForm());
    }

    private Form<EmailTemplateEntity> createEditEmailTemplateForm() {
        Form<EmailTemplateEntity> form = newEditEmailTemplateForm();
        form.add(createSubjectField());
        form.add(createContentField());
        form.setOutputMarkupId(true);
        return form;
    }

    private Form<EmailTemplateEntity> newEditEmailTemplateForm() {
        return new Form<EmailTemplateEntity>("form", new CompoundPropertyModel<EmailTemplateEntity>(emailTemplateModel)) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit() {
                emailService.save(emailTemplateModel.getObject());
                setRedirect(false);
                info(EmailTemplateEditPage.this.getString("msg.saved"));
                setResponsePage(EmailTemplatePage.class);
            }
        };
    }

    private FormComponent<String> createContentField() {
        FormComponent<String> fc = new BasicRichTextArea("content", true);
        fc.setRequired(true);
        return fc;
    }

    private FormComponent<String> createSubjectField() {
        return new RequiredTextField<String>("subject");
    }
}
