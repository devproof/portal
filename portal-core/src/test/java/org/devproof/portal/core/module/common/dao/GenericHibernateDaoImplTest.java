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
package org.devproof.portal.core.module.common.dao;

import junit.framework.TestCase;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.devproof.portal.core.module.user.service.UsernameResolver;
import org.easymock.EasyMock;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
/**
 * @author Carsten Hufe
 */
public class GenericHibernateDaoImplTest extends TestCase {
	private GenericHibernateDaoImpl<EmailTemplateEntity, Integer> impl;
	private SessionFactory sessionFactory;
	private Session session;
	private Query query;
	private UsernameResolver usernameResolver;

	@Override
	public void setUp() throws Exception {
		sessionFactory = EasyMock.createMock(SessionFactory.class);
		session = EasyMock.createMock(Session.class);
		query = EasyMock.createMock(Query.class);
		usernameResolver = EasyMock.createMock(UsernameResolver.class);
		impl = new GenericHibernateDaoImpl<EmailTemplateEntity, Integer>(EmailTemplateEntity.class);
		impl.setSessionFactory(sessionFactory);
		impl.setUsernameResolver(usernameResolver);
		expect(session.getSessionFactory()).andReturn(sessionFactory);
		expect(sessionFactory.openSession()).andReturn(session);
		SessionHolder sessionHolder = new SessionHolder(session);
		TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
		expect(session.isOpen()).andReturn(false);
		expect(session.getSessionFactory()).andReturn(sessionFactory);
	}

	public void testFindById() {
		EmailTemplateEntity expectedTemplates = newEmailTemplate();
		expect(session.get(EmailTemplateEntity.class, 1)).andReturn(expectedTemplates);
		replay(sessionFactory, session);
		EmailTemplateEntity newTemplate = impl.findById(1);
		assertEquals(expectedTemplates, newTemplate);
		verify(session, sessionFactory);
	}

	public void testSave() {
		EmailTemplateEntity template = newEmailTemplate();
		expect(session.beginTransaction()).andReturn(null);
		expect(session.merge(template)).andReturn(1);
		expect(usernameResolver.getUsername()).andReturn("testuser");
		replay(sessionFactory, session, query, usernameResolver);
		impl.save(template);
		verify(session, sessionFactory, query, usernameResolver);
		assertNotNull(template.getCreatedAt());
		assertEquals("testuser", template.getCreatedBy());
		assertNotNull(template.getModifiedAt());
		assertEquals("testuser", template.getModifiedBy());
	}

	public void testRefresh() {
		EmailTemplateEntity template = newEmailTemplate();
		session.refresh(template);
		replay(sessionFactory, session, query);
		impl.refresh(template);
		verify(session, sessionFactory, query);
	}

	public void testDelete() {
		EmailTemplateEntity template = newEmailTemplate();
		expect(session.beginTransaction()).andReturn(null);
		session.delete(template);
		replay(sessionFactory, session, query);
		impl.delete(template);
		verify(session, sessionFactory, query);
	}

	public void testExecuteFinder_UniqueResult() throws Exception {
		EmailTemplateEntity expectedTemplate = newEmailTemplate();
		expect(session.createQuery("Select e from EmailTemplateEntity e where id = ?")).andReturn(query);
		expect(query.setParameter(0, "fakeValue")).andReturn(query);
		expect(query.setFirstResult(0)).andReturn(query);
		expect(query.setMaxResults(10)).andReturn(query);
		expect(query.uniqueResult()).andReturn(expectedTemplate);
		replay(sessionFactory, session, query);
		Method method = this.getClass().getMethod("methodObject");
		Object template = impl.executeFinder("Select e from EmailTemplateEntity e where id = ?", new Object[] { "fakeValue" },
				method, 0, 10);
		verify(session, sessionFactory, query);
		assertEquals(expectedTemplate, template);
	}

	public void testExecuteFinder_ResultList() throws Exception {
		List<EmailTemplateEntity> expectedTemplates = Arrays.asList(newEmailTemplate());
		expect(session.createQuery("Select e from EmailTemplateEntity e where id = ?")).andReturn(query);
		expect(query.setParameter(0, "fakeValue")).andReturn(query);
		expect(query.setFirstResult(0)).andReturn(query);
		expect(query.setMaxResults(10)).andReturn(query);
		expect(query.list()).andReturn(expectedTemplates);
		replay(sessionFactory, session, query);
		Method method = this.getClass().getMethod("methodList");
		List<?> templates = (List<?>) impl.executeFinder("Select e from $TYPE e where id = ?",
				new Object[] { "fakeValue" }, method, 0, 10);
		verify(session, sessionFactory, query);
		assertEquals(expectedTemplates.get(0), templates.get(0));
	}

	public void testExecuteUpdate() {
		expect(session.createQuery("update EmailTemplateEntity set id = 'someValue' where id = ?"))
				.andReturn(query);
		expect(query.setParameter(0, "fakeValue")).andReturn(query);
		expect(query.executeUpdate()).andReturn(0);
		replay(sessionFactory, session, query);
		impl.executeUpdate("update $TYPE set id = 'someValue' where id = ?", new Object[] { "fakeValue" });
		verify(session, sessionFactory, query);
	}

	private EmailTemplateEntity newEmailTemplate() {
		EmailTemplateEntity expectedConfig = new EmailTemplateEntity();
		expectedConfig.setId(1);
		return expectedConfig;
	}


	public List<?> methodList() {
		return null;
	}

	public EmailTemplateEntity methodObject() {
		return null;
	}    
}
