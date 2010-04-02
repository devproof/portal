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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.BeanJoin;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.easymock.EasyMock;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * @author Carsten Hufe
 */
public class DataProviderDaoImplTest extends TestCase {
	private DataProviderDaoImpl<EmailTemplateEntity> impl;
	private SessionFactory sessionFactory;
	private Session session;
	private Query query;

	@Override
	protected void setUp() throws Exception {
		sessionFactory = EasyMock.createMock(SessionFactory.class);
		session = EasyMock.createMock(Session.class);
		impl = new DataProviderDaoImpl<EmailTemplateEntity>();
		impl.setSessionFactory(sessionFactory);
		query = EasyMock.createMock(Query.class);
		EasyMock.expect(session.getSessionFactory()).andReturn(sessionFactory);
		EasyMock.expect(sessionFactory.openSession()).andReturn(session);
		SessionHolder sessionHolder = new SessionHolder(session);
		TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
		EasyMock.expect(session.isOpen()).andReturn(false);
		EasyMock.expect(session.getSessionFactory()).andReturn(sessionFactory);
	}

	public void testFindById() {
		EmailTemplateEntity expectedTemplates = newEmailTemplate();
		EasyMock.expect(session.get(EmailTemplateEntity.class, 1)).andReturn(expectedTemplates);
		EasyMock.replay(sessionFactory, session);
		EmailTemplateEntity newTemplate = impl.findById(EmailTemplateEntity.class, 1);
		assertEquals(expectedTemplates, newTemplate);
		EasyMock.verify(session, sessionFactory);
	}

	public void testFindAll_byClass() {
		List<EmailTemplateEntity> expectedTemplates = Arrays.asList(newEmailTemplate());
		EasyMock.expect(session.createQuery("Select distinct(e) from EmailTemplateEntity e")).andReturn(query);
		EasyMock.expect(query.setCacheable(true)).andReturn(query);
		EasyMock.expect(query.setCacheMode(null)).andReturn(query);
		EasyMock.expect(query.setCacheRegion(CommonConstants.QUERY_CORE_CACHE_REGION)).andReturn(query);
		EasyMock.expect(query.list()).andReturn(expectedTemplates);
		EasyMock.replay(sessionFactory, session, query);
		List<EmailTemplateEntity> templates = impl.findAll(EmailTemplateEntity.class);
		assertEquals(expectedTemplates.get(0).getId(), templates.get(0).getId());
		EasyMock.verify(session, sessionFactory, query);
	}

	public void testFindAll_byClassLimited() {
		List<EmailTemplateEntity> expectedTemplates = Arrays.asList(newEmailTemplate());
		EasyMock.expect(session.createQuery("Select distinct(e) from EmailTemplateEntity e")).andReturn(query);
		EasyMock.expect(query.setCacheable(true)).andReturn(query);
		EasyMock.expect(query.setCacheMode(null)).andReturn(query);
		EasyMock.expect(query.setCacheRegion(CommonConstants.QUERY_CORE_CACHE_REGION)).andReturn(query);
		EasyMock.expect(query.setFirstResult(20)).andReturn(query);
		EasyMock.expect(query.setMaxResults(10)).andReturn(query);
		EasyMock.expect(query.list()).andReturn(expectedTemplates);
		EasyMock.replay(sessionFactory, session, query);
		List<EmailTemplateEntity> templates = impl.findAll(EmailTemplateEntity.class, 20, 10);
		assertEquals(expectedTemplates.get(0).getId(), templates.get(0).getId());
		EasyMock.verify(session, sessionFactory, query);
	}

	public void testFindAllWithQuery() {
		List<EmailTemplateEntity> expectedTemplates = Arrays.asList(newEmailTemplate());
		TestQuery testQuery = new TestQuery();
		testQuery.setAllTextFields("foobar");
		List<String> prefetch = Arrays.asList("prefetched_field");
		EasyMock.expect(
				session.createQuery("Select distinct(e) from EmailTemplateEntity e"
						+ "  left join fetch e.prefetched_field  left join e.allRights vr left join e.tags t"
						+ "  where e.headline like ? order by e.subject ASC")).andReturn(query);
		EasyMock.expect(query.setParameter(0, "foobar")).andReturn(query);
		EasyMock.expect(query.list()).andReturn(expectedTemplates);
		EasyMock.expect(query.setCacheable(true)).andReturn(query);
		EasyMock.expect(query.setCacheMode(null)).andReturn(query);
		EasyMock.expect(query.setCacheRegion(CommonConstants.QUERY_CORE_CACHE_REGION)).andReturn(query);
		EasyMock.expect(query.setFirstResult(20)).andReturn(query);
		EasyMock.expect(query.setMaxResults(10)).andReturn(query);
		EasyMock.replay(sessionFactory, session, query);
		List<EmailTemplateEntity> templates = impl.findAllWithQuery(EmailTemplateEntity.class, "subject", true, 20, 10,
				testQuery, prefetch);
		assertEquals(expectedTemplates.get(0).getId(), templates.get(0).getId());
		EasyMock.verify(session, sessionFactory, query);
	}

	public void testGetSize_byBeanQuery() {
		TestQuery testQuery = new TestQuery();
		testQuery.setAllTextFields("foobar");
		EasyMock.expect(
				session.createQuery("Select count(distinct e) from EmailTemplateEntity e"
						+ "  left join e.allRights vr left join e.tags t" + "  where e.headline like ?")).andReturn(
				query);
		EasyMock.expect(query.setParameter(0, "foobar")).andReturn(query);
		EasyMock.expect(query.uniqueResult()).andReturn(2l);
		EasyMock.replay(sessionFactory, session, query);
		long size = impl.getSize(EmailTemplateEntity.class, testQuery);
		assertEquals(2, size);
		EasyMock.verify(session, sessionFactory, query);
	}

	public void testGetSize_withCountQuery() {
		TestQuery testQuery = new TestQuery();
		testQuery.setAllTextFields("foobar");
		EasyMock.expect(
				session.createQuery("Select count(something) from EmailTemplateEntity e"
						+ "  left join e.allRights vr left join e.tags t" + "  where e.headline like ?")).andReturn(
				query);
		EasyMock.expect(query.setParameter(0, "foobar")).andReturn(query);
		EasyMock.expect(query.uniqueResult()).andReturn(2l);
		EasyMock.replay(sessionFactory, session, query);
		long size = impl.getSize(EmailTemplateEntity.class, "count(something)", testQuery);
		assertEquals(2, size);
		EasyMock.verify(session, sessionFactory, query);
	}

	private EmailTemplateEntity newEmailTemplate() {
		EmailTemplateEntity expectedConfig = new EmailTemplateEntity();
		expectedConfig.setId(1);
		return expectedConfig;
	}

	@BeanJoin("left join e.allRights vr left join e.tags t")
	public class TestQuery implements SearchQuery {
		private static final long serialVersionUID = 1L;
		private Integer id;
		private RoleEntity role;
		private String allTextFields;

		@BeanQuery("vr in(select rt from RoleEntity r join r.rights rt where r = ?)")
		public RoleEntity getRole() {
			return role;
		}

		public void setRole(RoleEntity role) {
			this.role = role;
		}

		@BeanQuery("e.headline like ?")
		public String getAllTextFields() {
			return allTextFields;
		}

		public void setAllTextFields(String allTextFields) {
			this.allTextFields = allTextFields;
		}

		@BeanQuery("e.id = ?")
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public void clearSelection() {
		}
	}

}
