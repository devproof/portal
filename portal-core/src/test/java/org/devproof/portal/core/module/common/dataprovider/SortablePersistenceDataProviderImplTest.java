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
package org.devproof.portal.core.module.common.dataprovider;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.model.IModel;
import org.devproof.portal.core.module.common.dao.DataProviderDao;
import org.devproof.portal.core.module.common.query.IQuery;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class SortablePersistenceDataProviderImplTest extends TestCase {
	private SortablePersistenceDataProviderImpl<EmailTemplateEntity> impl;
	private DataProviderDao<EmailTemplateEntity> dataProviderDaoMock;
	private IQuery queryMock;

	@Override
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		dataProviderDaoMock = EasyMock.createMock(DataProviderDao.class);
		queryMock = EasyMock.createMock(IQuery.class);
		impl = new SortablePersistenceDataProviderImpl<EmailTemplateEntity>();
		impl.setEntityClass(EmailTemplateEntity.class);
		impl.setSort("subject", true);
		impl.setDataProviderDao(dataProviderDaoMock);
		impl.setQueryObject(queryMock);
	}

	public void testIterator_WithPrefetch() {
		List<String> prefetch = Arrays.asList("prefetch");
		impl.setPrefetch(prefetch);
		EmailTemplateEntity expectedTemplate = new EmailTemplateEntity();
		expectedTemplate.setId(5);
		List<EmailTemplateEntity> templates = Arrays.asList(expectedTemplate);
		EasyMock.expect(
				dataProviderDaoMock.findAllWithQuery(EmailTemplateEntity.class, "subject", true, 20, 10, queryMock,
						prefetch)).andReturn(templates);
		EasyMock.replay(dataProviderDaoMock, queryMock);
		Iterator<? extends EmailTemplateEntity> iterator = impl.iterator(20, 10);
		assertEquals(expectedTemplate.getId(), iterator.next().getId());
		EasyMock.verify(dataProviderDaoMock, queryMock);
	}

	public void testIterator_WithoutPrefetch() {
		EmailTemplateEntity expectedTemplate = new EmailTemplateEntity();
		expectedTemplate.setId(5);
		List<EmailTemplateEntity> templates = Arrays.asList(expectedTemplate);
		EasyMock.expect(
				dataProviderDaoMock.findAllWithQuery(EmailTemplateEntity.class, "subject", true, 20, 10, queryMock,
						null)).andReturn(templates);
		EasyMock.replay(dataProviderDaoMock, queryMock);
		Iterator<? extends EmailTemplateEntity> iterator = impl.iterator(20, 10);
		assertEquals(expectedTemplate.getId(), iterator.next().getId());
		EasyMock.verify(dataProviderDaoMock, queryMock);
	}

	public void testModel() {
		IModel<EmailTemplateEntity> model = impl.model(new EmailTemplateEntity());
		assertNotNull(model);
		assertNotNull(model.getObject());
	}

	public void testSize_WithCountQuery() {
		EasyMock.expect(dataProviderDaoMock.getSize(EmailTemplateEntity.class, "count(something)", queryMock))
				.andReturn(4);
		EasyMock.replay(dataProviderDaoMock, queryMock);
		impl.setCountQuery("count(something)");
		assertEquals(4, impl.size());
		EasyMock.verify(dataProviderDaoMock, queryMock);
	}

	public void testSize_WithoutCountQuery() {
		EasyMock.expect(dataProviderDaoMock.getSize(EmailTemplateEntity.class, queryMock)).andReturn(4);
		EasyMock.replay(dataProviderDaoMock, queryMock);
		assertEquals(4, impl.size());
		EasyMock.verify(dataProviderDaoMock, queryMock);
	}
}
