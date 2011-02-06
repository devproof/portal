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
package org.devproof.portal.core.mock;

import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.entity.EmailTemplate;
import org.devproof.portal.core.module.email.service.EmailService;

import java.io.Serializable;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class EmailServiceMock implements EmailService, Serializable {
    private static final long serialVersionUID = 1L;
    private EmailPlaceholderBean emailPlaceholderBean;

    public EmailPlaceholderBean getEmailPlaceholderBean() {
        return emailPlaceholderBean;
    }

    @Override
    public EmailTemplate newEmailTemplateEntity() {
        return null;
    }

    @Override
    public void sendEmail(EmailTemplate template, EmailPlaceholderBean placeholder) {
        emailPlaceholderBean = placeholder;
    }

    @Override
    public void sendEmail(Integer templateId, EmailPlaceholderBean placeholder) {
        emailPlaceholderBean = placeholder;
    }

    @Override
    public void delete(EmailTemplate entity) {

    }

    @Override
    public List<EmailTemplate> findAll() {
        return null;
    }

    @Override
    public EmailTemplate findById(Integer id) {
        return null;
    }

    @Override
    public void save(EmailTemplate entity) {

    }
}
