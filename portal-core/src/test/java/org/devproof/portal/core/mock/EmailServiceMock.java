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
package org.devproof.portal.core.mock;

import java.io.Serializable;
import java.util.List;

import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.devproof.portal.core.module.email.service.EmailService;

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
	public EmailTemplateEntity newEmailTemplateEntity() {
		return null;
	}

	@Override
	public void sendEmail(EmailTemplateEntity template, EmailPlaceholderBean placeholder) {
		emailPlaceholderBean = placeholder;
	}

	@Override
	public void sendEmail(Integer templateId, EmailPlaceholderBean placeholder) {
		emailPlaceholderBean = placeholder;
	}

	@Override
	public void delete(EmailTemplateEntity entity) {

	}

	@Override
	public List<EmailTemplateEntity> findAll() {
		return null;
	}

	@Override
	public EmailTemplateEntity findById(Integer id) {
		return null;
	}

	@Override
	public void save(EmailTemplateEntity entity) {

	}
}
