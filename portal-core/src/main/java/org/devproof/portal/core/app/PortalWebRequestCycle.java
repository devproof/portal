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

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Hibernate transaction and session management
 * 
 * @author Carsten Hufe
 * 
 */
public class PortalWebRequestCycle extends WebRequestCycle {

	private final SessionFactory sessionFactory;

	public PortalWebRequestCycle(final WebApplication application, final WebRequest request, final Response response, final ApplicationContext context) {
		super(application, request, response);
		this.sessionFactory = (SessionFactory) context.getBean("sessionFactory");
	}

	@Override
	protected void onBeginRequest() {
		// getSession should bind the resource
		final Session session = SessionFactoryUtils.getSession(this.sessionFactory, true);
		SessionHolder holder = new SessionHolder(session);
		session.setFlushMode(FlushMode.AUTO);
		if (!TransactionSynchronizationManager.hasResource(this.sessionFactory)) {
			TransactionSynchronizationManager.bindResource(this.sessionFactory, holder);
		}
		super.onBeginRequest();
	}

	@Override
	protected void onEndRequest() {
		if (TransactionSynchronizationManager.hasResource(this.sessionFactory)) {
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(this.sessionFactory);
			if (sessionHolder.getTransaction() != null && !sessionHolder.getTransaction().wasRolledBack()) {
				sessionHolder.getTransaction().commit();
			}
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
}
