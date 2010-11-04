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

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.entity.Modification;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.junit.Before;
import org.junit.Test;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Carsten Hufe
 */
public class FinderDispatcherGenericRepositoryImplTest {
    private FinderDispatcherGenericRepositoryImpl<TestEntity, Integer> impl;
    private TestRepository testDao;
    private CrudRepository<TestEntity, Integer> crudRepository;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        SessionFactory sessionFactory = createMock(SessionFactory.class);
        Session session = createMock(Session.class);
        crudRepository = createMock(CrudRepository.class);
        impl = new FinderDispatcherGenericRepositoryImpl<TestEntity, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            protected CrudRepository<TestEntity, Integer> createGenericHibernateDao() {
                return crudRepository;
            }

        };
        impl.setDaoInterface(TestRepository.class);
        impl.setEntityClass(TestEntity.class);
        impl.setSessionFactory(sessionFactory);
        testDao = (TestRepository) impl.getObject();
        expect(session.getSessionFactory()).andReturn(sessionFactory);
        expect(sessionFactory.openSession()).andReturn(session);
        SessionHolder sessionHolder = new SessionHolder(session);
        TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);
        expect(session.isOpen()).andReturn(false);
        expect(session.getSessionFactory()).andReturn(sessionFactory);
    }

    @Test
    public void testGetObject_delegateSave() {
        TestEntity entity = createEntity();
        expect(crudRepository.save(entity)).andReturn(entity);
        replay(crudRepository);
        testDao.save(entity);
        verify(crudRepository);
    }

    @Test
    public void testGetObject_delegateDelete() {
        TestEntity entity = createEntity();
        crudRepository.delete(entity);
        replay(crudRepository);
        testDao.delete(entity);
        verify(crudRepository);
    }

    @Test
    public void testGetObject_delegateRefresh() {
        TestEntity entity = createEntity();
        crudRepository.refresh(entity);
        replay(crudRepository);
        testDao.refresh(entity);
        verify(crudRepository);
    }

    @Test
    public void testGetObject_delegateFindById() {
        TestEntity expectedEntity = createEntity();
        expect(crudRepository.findById(1)).andReturn(expectedEntity);
        replay(crudRepository);
        TestEntity entity = testDao.findById(1);
        assertEquals(expectedEntity, entity);
        verify(crudRepository);
    }

    @Test
    public void testGetObject_queryAnnotation() throws Exception {
        TestEntity expectedEntity = createEntity();
        expect(crudRepository.executeFinder(eq("select t from TestEntity t where t.contentId = ?"), (Object[]) anyObject(), (Method) anyObject(), (Integer) eq(null), (Integer) eq(null))).andReturn(expectedEntity);
        replay(crudRepository);
        TestEntity entity = testDao.findByContentId("foobar");
        assertEquals(expectedEntity, entity);
        verify(crudRepository);
    }

    @Test
    public void testGetObject_bulkUpdate() {
        crudRepository.executeUpdate(eq("update TestEntity with something where contentId = ?"), (Object[]) anyObject());
        replay(crudRepository);
        testDao.updateWithSomething("foobar");
        verify(crudRepository);
    }

    @Test
    public void testGetObject_delegateToImplMethod() {
        TestRepository serviceImpl = createMock(TestRepository.class);
        impl.setServicesImpl(serviceImpl);
        serviceImpl.delegateToImpl();
        replay(crudRepository, serviceImpl);
        testDao.delegateToImpl();
        verify(crudRepository, serviceImpl);
    }

    private TestEntity createEntity() {
        TestEntity entity = new TestEntity();
        entity.setId(1);
        return entity;
    }

    public interface TestRepository extends CrudRepository<TestEntity, Integer> {
        @Query("select t from TestEntity t where t.contentId = ?")
        public TestEntity findByContentId(String contentId);

        @BulkUpdate("update TestEntity with something where contentId = ?")
        public void updateWithSomething(String contentId);

        public void delegateToImpl();
    }

    public static class TestEntity extends Modification {
        private static final long serialVersionUID = 1L;
        private Integer id;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            TestEntity other = (TestEntity) obj;
            if (id == null) {
                if (other.id != null) return false;
            } else if (!id.equals(other.id)) return false;
            return true;
        }
    }
}
