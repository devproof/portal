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
package org.devproof.portal.core.module.common.repository;

import org.devproof.portal.core.module.email.entity.EmailTemplate;
import org.devproof.portal.core.module.user.service.UsernameResolver;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Carsten Hufe
 */
public class GenericHibernateRepositoryImplTest {
	private GenericHibernateRepositoryImpl<EmailTemplate, Integer> impl;
	private SessionFactory sessionFactory;
	private Session session;
	private Query query;
	private UsernameResolver usernameResolver;

	@Before
	public void setUp() throws Exception {
		sessionFactory = createMock(SessionFactory.class);
		session = createMock(Session.class);
		query = createMock(Query.class);
		usernameResolver = createMock(UsernameResolver.class);
		impl = new GenericHibernateRepositoryImpl<EmailTemplate, Integer>(EmailTemplate.class);
		impl.setSessionFactory(sessionFactory);
		impl.setUsernameResolver(usernameResolver);
		expect(session.getSessionFactory()).andReturn(sessionFactory);
		expect(sessionFactory.openSession()).andReturn(session);
		SessionHolder sessionHolder = new SessionHolder(session);
		TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
		expect(session.isOpen()).andReturn(false);
		expect(session.getSessionFactory()).andReturn(sessionFactory);
	}

	@Test
	public void testFindById() {
		EmailTemplate expectedTemplates = newEmailTemplate();
		expect(session.get(EmailTemplate.class, 1)).andReturn(expectedTemplates);
		replay(sessionFactory, session);
		EmailTemplate newTemplate = impl.findById(1);
		assertEquals(expectedTemplates, newTemplate);
		verify(session, sessionFactory);
	}

	@Test
	public void testSave() {
		EmailTemplate template = newEmailTemplate();
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

	@Test
	public void testRefresh() {
		EmailTemplate template = newEmailTemplate();
		session.refresh(template);
		replay(sessionFactory, session, query);
		impl.refresh(template);
		verify(session, sessionFactory, query);
	}

	@Test
	public void testDelete() {
		EmailTemplate template = newEmailTemplate();
		expect(session.beginTransaction()).andReturn(null);
		session.delete(template);
		replay(sessionFactory, session, query);
		impl.delete(template);
		verify(session, sessionFactory, query);
	}

	@Test
	public void testExecuteFinder_UniqueResult() throws Exception {
		EmailTemplate expectedTemplate = newEmailTemplate();
		expect(session.createQuery("Select e from EmailTemplate e where id = ?")).andReturn(query);
		expect(query.setParameter(0, "fakeValue")).andReturn(query);
		expect(query.setFirstResult(0)).andReturn(query);
		expect(query.setMaxResults(10)).andReturn(query);
		expect(query.uniqueResult()).andReturn(expectedTemplate);
		replay(sessionFactory, session, query);
		Method method = this.getClass().getMethod("methodObject");
		Object template = impl.executeFinder("Select e from EmailTemplate e where id = ?",
				new Object[] { "fakeValue" }, method, 0, 10);
		verify(session, sessionFactory, query);
		assertEquals(expectedTemplate, template);
	}

	@Test
	public void testExecuteFinder_ResultList() throws Exception {
		List<EmailTemplate> expectedTemplates = Arrays.asList(newEmailTemplate());
		expect(session.createQuery("Select e from EmailTemplate e where id = ?")).andReturn(query);
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

	@Test
	public void testExecuteUpdate() {
		expect(session.createQuery("update EmailTemplate set id = 'someValue' where id = ?")).andReturn(query);
		expect(query.setParameter(0, "fakeValue")).andReturn(query);
		expect(query.executeUpdate()).andReturn(0);
		replay(sessionFactory, session, query);
		impl.executeUpdate("update $TYPE set id = 'someValue' where id = ?", new Object[] { "fakeValue" });
		verify(session, sessionFactory, query);
	}

	private EmailTemplate newEmailTemplate() {
		EmailTemplate expectedConfig = new EmailTemplate();
		expectedConfig.setId(1);
		return expectedConfig;
	}

	public List<?> methodList() {
		return null;
	}

	public EmailTemplate methodObject() {
		return null;
	}
}
