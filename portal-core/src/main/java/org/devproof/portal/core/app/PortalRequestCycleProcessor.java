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
package org.devproof.portal.core.app;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import org.apache.wicket.Page;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.protocol.http.PageExpiredException;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.email.bean.EmailPlaceholderBean;
import org.devproof.portal.core.module.email.service.EmailService;
import org.devproof.portal.core.module.user.entity.UserEntity;
import org.devproof.portal.core.module.user.service.UserService;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Rollback of transaction when a runtime exception occurs Inform the admin
 * about the runtime exception
 * 
 * @author Carsten Hufe
 * 
 */
public class PortalRequestCycleProcessor extends WebRequestCycleProcessor {
	private final SessionFactory sessionFactory;
	private final ConfigurationService configurationService;
	private final UserService userService;
	private final EmailService emailService;

	public PortalRequestCycleProcessor(final ApplicationContext context, final boolean production) {
		sessionFactory = (SessionFactory) context.getBean("sessionFactory");
		configurationService = (ConfigurationService) context.getBean("configurationService");
		userService = (UserService) context.getBean("userService");
		emailService = (EmailService) context.getBean("emailService");
	}

	@Override
	protected Page onRuntimeException(final Page page, final RuntimeException e) {
		// send mail to the admin!
		if (!(e instanceof PageExpiredException) && !(e instanceof UnauthorizedInstantiationException)) {
			Integer templateId = configurationService.findAsInteger(CommonConstants.CONF_UNKNOWN_ERROR_EMAIL);

			final Writer content = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(content);
			e.printStackTrace(printWriter);
			sendEmailToUsers(templateId, content.toString());

		}
		// does the rollback if there is a runtime exception
		SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		if (holder.getTransaction() != null) {
			holder.getTransaction().rollback();
		}
		return super.onRuntimeException(page, e);
	}

	private void sendEmailToUsers(final Integer templateId, final String content) {
		EmailPlaceholderBean placeholder = new EmailPlaceholderBean();
		placeholder.setContent(content);
		List<UserEntity> users = userService.findUserWithRight("emailnotification.unknown.application.error");
		for (UserEntity user : users) {
			placeholder.setUsername(user.getUsername());
			placeholder.setFirstname(user.getFirstname());
			placeholder.setLastname(user.getLastname());
			placeholder.setEmail(user.getEmail());
			emailService.sendEmail(templateId, placeholder);
		}
	}
}
