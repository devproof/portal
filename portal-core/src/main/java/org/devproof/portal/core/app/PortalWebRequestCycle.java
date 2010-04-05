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
package org.devproof.portal.core.app;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
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

	private SessionFactory sessionFactory;

	public PortalWebRequestCycle(WebApplication application, WebRequest request, Response response,
			ApplicationContext context) {
		super(application, request, response);
		sessionFactory = (SessionFactory) context.getBean("sessionFactory");
	}

	@Override
	protected void onBeginRequest() {
		// getSession should bind the resource
		openHibernateSessionInView();
		super.onBeginRequest();
	}

	@Override
	public void detach() {
		closeHibernateSessionInView();
		super.detach();
	}

	private void openHibernateSessionInView() {
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		SessionHolder holder = new SessionHolder(session);
		if (!TransactionSynchronizationManager.hasResource(sessionFactory)) {
			TransactionSynchronizationManager.bindResource(sessionFactory, holder);
		}
	}

	private void closeHibernateSessionInView() {
		if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
					.unbindResource(sessionFactory);
			if (sessionHolder.getTransaction() != null && !sessionHolder.getTransaction().wasRolledBack()
					&& !sessionHolder.getTransaction().wasCommitted()) {
				sessionHolder.getTransaction().commit();
			}
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}

}
