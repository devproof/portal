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
package org.devproof.portal.core.module.email.service;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;

import java.util.List;

/**
 * Methods to send emails
 * 
 * @author Carsten Hufe
 */
public interface EmailService extends CrudService<EmailTemplateEntity, Integer> {
	/**
	 * Returns all email templates
	 */
	List<EmailTemplateEntity> findAll();

	/**
	 * Returns a new {@link EmailTemplateEntity}
	 */
	EmailTemplateEntity newEmailTemplateEntity();

	/**
	 * Send a email to a user
	 */
	void sendEmail(EmailTemplateEntity template, EmailPlaceholderBean placeholder);

	/**
	 * Send a email to a user
	 */
	void sendEmail(Integer templateId, EmailPlaceholderBean placeholder);
}
