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
package org.devproof.portal.core.module.common.dataprovider;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.devproof.portal.core.module.common.repository.DataProviderRepository;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.email.entity.EmailTemplateEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Carsten Hufe
 */
public class SortablePersistenceDataProviderImplTest {
    private SortablePersistenceDataProviderImpl<EmailTemplateEntity, SearchQuery> impl;
    private DataProviderRepository<EmailTemplateEntity> dataProviderRepositoryMock;
    private SearchQuery queryMock;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        dataProviderRepositoryMock = createMock(DataProviderRepository.class);
        queryMock = createMock(SearchQuery.class);
        impl = new SortablePersistenceDataProviderImpl<EmailTemplateEntity, SearchQuery>() {
            private static final long serialVersionUID = 1L;

            @Override
            public IModel<SearchQuery> getSearchQueryModel() {
                return Model.of(queryMock);
            }
        };
        impl.setEntityClass(EmailTemplateEntity.class);
        impl.setSort("subject", true);
        impl.setDataProviderDao(dataProviderRepositoryMock);
    }

    @Test
    public void testIterator_WithPrefetch() {
        List<String> prefetch = Arrays.asList("prefetch");
        impl.setPrefetch(prefetch);
        EmailTemplateEntity expectedTemplate = new EmailTemplateEntity();
        expectedTemplate.setId(5);
        List<EmailTemplateEntity> templates = Arrays.asList(expectedTemplate);
        expect(dataProviderRepositoryMock.findAllWithQuery(EmailTemplateEntity.class, "subject", true, 20, 10, queryMock, prefetch)).andReturn(templates);
        replay(dataProviderRepositoryMock, queryMock);
        Iterator<? extends EmailTemplateEntity> iterator = impl.iterator(20, 10);
        assertEquals(expectedTemplate.getId(), iterator.next().getId());
        verify(dataProviderRepositoryMock, queryMock);
    }

    @Test
    public void testIterator_WithoutPrefetch() {
        EmailTemplateEntity expectedTemplate = new EmailTemplateEntity();
        expectedTemplate.setId(5);
        List<EmailTemplateEntity> templates = Arrays.asList(expectedTemplate);
        expect(dataProviderRepositoryMock.findAllWithQuery(EmailTemplateEntity.class, "subject", true, 20, 10, queryMock, null)).andReturn(templates);
        replay(dataProviderRepositoryMock, queryMock);
        Iterator<? extends EmailTemplateEntity> iterator = impl.iterator(20, 10);
        assertEquals(expectedTemplate.getId(), iterator.next().getId());
        verify(dataProviderRepositoryMock, queryMock);
    }

    @Test
    public void testModel() {
        IModel<EmailTemplateEntity> model = impl.model(new EmailTemplateEntity());
        assertNotNull(model);
        assertNotNull(model.getObject());
    }

    @Test
    public void testSize_WithCountQuery() {
        expect(dataProviderRepositoryMock.getSize(EmailTemplateEntity.class, "count(something)", queryMock)).andReturn(4);
        replay(dataProviderRepositoryMock, queryMock);
        impl.setCountQuery("count(something)");
        assertEquals(4, impl.size());
        verify(dataProviderRepositoryMock, queryMock);
    }

    @Test
    public void testSize_WithoutCountQuery() {
        expect(dataProviderRepositoryMock.getSize(EmailTemplateEntity.class, queryMock)).andReturn(4);
        replay(dataProviderRepositoryMock, queryMock);
        assertEquals(4, impl.size());
        verify(dataProviderRepositoryMock, queryMock);
    }
}
