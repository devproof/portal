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
package org.devproof.portal.core.module.common.dao;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.user.service.UsernameResolver;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The executing class for the generic dao:
 * <p/>
 * 1. executes the generic dao methods 2. executes generic queries 3. executes
 * generic bulk updates 4. executes own implemented dao methods (from
 * servicesImpl) if no Query or BulkUpdate annotation exist 5. opens transaction
 * on the first change call (save, update, merge, delete, bulkupdate) 6. opens a
 * session if no ones exists and closes it after the call
 *
 * @author Carsten Hufe
 * @param <T>
 * entity type
 * @param <PK>
 * primary key type
 */
public class FinderDispatcherGenericDaoImpl<T, PK extends Serializable> extends HibernateDaoSupport implements FactoryBean, Serializable {

    private static final long serialVersionUID = -3752572093862325307L;

    private Object servicesImpl;
    private Class<T> entityClass;
    private Class<?> daoInterface;
    private UsernameResolver usernameResolver;

    public Object getObject() throws Exception {
        ProxyFactory result = new ProxyFactory();
        GenericDao<T, PK> genericDao = createGenericHibernateDao();
        result.setTarget(genericDao);
        result.setInterfaces(new Class[]{daoInterface});
        result.addAdvice(createGenericDaoInterceptor());
        return result.getProxy();
    }

    protected GenericDao<T, PK> createGenericHibernateDao() {
        GenericHibernateDaoImpl<T, PK> genericDao = new GenericHibernateDaoImpl<T, PK>(entityClass);
        genericDao.setSessionFactory(getSessionFactory());
        genericDao.setHibernateTemplate(getHibernateTemplate());
        genericDao.setUsernameResolver(usernameResolver);
        return genericDao;
    }

    private MethodInterceptor createGenericDaoInterceptor() {
        return new MethodInterceptor() {
            public Object invoke(MethodInvocation invocation) throws Throwable {
                /*
                     * If the session will be opened at this place, the same method
                     * closes the session and transaction
                     */
                boolean isSessionAvailable = isSessionAvailable();
                if (!isSessionAvailable) {
                    openSession();
                }
                Object result = evaluateMethodInvocation(invocation);
                if (!isSessionAvailable) {
                    closeSession();
                }
                return result;

            }

            private Object evaluateMethodInvocation(MethodInvocation invocation) throws Throwable {
                Object result = null;
                Method method = invocation.getMethod();
                if (method.isAnnotationPresent(Query.class)) {
                    result = executeQuery(invocation);
                } else if (method.isAnnotationPresent(BulkUpdate.class)) {
                    executeBulkUpdate(invocation);
                } else {
                    result = delegateToServiceMethod(invocation);
                }
                return result;
            }

            private Object delegateToServiceMethod(MethodInvocation invocation) throws Throwable {
                Method serviceMethod = FinderDispatcherGenericDaoImpl.this.servicesImpl != null ? FinderDispatcherGenericDaoImpl.this.servicesImpl.getClass().getMethod(invocation.getMethod().getName(), invocation.getMethod().getParameterTypes()) : null;
                if (serviceMethod != null) {
                    return serviceMethod.invoke(FinderDispatcherGenericDaoImpl.this.servicesImpl, invocation.getArguments());
                } else {
                    // should be only save, update, delete from the generic
                    // dao
                    return invocation.proceed();
                }
            }

            private void executeBulkUpdate(MethodInvocation invocation) {
                openTransaction();
                Method method = invocation.getMethod();
                BulkUpdate bulkUpdate = method.getAnnotation(BulkUpdate.class);
                FinderExecutor target = (FinderExecutor) invocation.getThis();
                target.executeUpdate(bulkUpdate.value(), invocation.getArguments());
            }

            private boolean isSessionAvailable() {
                SessionFactory sessionFactory = FinderDispatcherGenericDaoImpl.this.getSessionFactory();
                return TransactionSynchronizationManager.hasResource(sessionFactory);
            }

            private void openTransaction() {
                SessionFactory sessionFactory = FinderDispatcherGenericDaoImpl.this.getSessionFactory();
                SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
                if (holder.getTransaction() == null) {
                    holder.setTransaction(holder.getSession().beginTransaction());
                }
            }

            private Object executeQuery(MethodInvocation invocation) {
                Method method = invocation.getMethod();
                Query query = method.getAnnotation(Query.class);
                FinderExecutor target = (FinderExecutor) invocation.getThis();
                if (query.limitClause()) {
                    Object orginal[] = invocation.getArguments();
                    int len = orginal.length - 2;
                    Object copy[] = new Object[len];
                    for (int i = 0; i < len; i++) {
                        copy[i] = orginal[i];
                    }
                    return target.executeFinder(query.value(), copy, method, (Integer) orginal[len], (Integer) orginal[len + 1]);
                } else {
                    return target.executeFinder(query.value(), invocation.getArguments(), method, null, null);
                }
            }

            private void closeSession() {
                SessionFactory sessionFactory = FinderDispatcherGenericDaoImpl.this.getSessionFactory();
                SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
                commitTransaction(sessionHolder);
                SessionFactoryUtils.closeSession(sessionHolder.getSession());
            }

            private void commitTransaction(SessionHolder sessionHolder) {
                if (sessionHolder.getTransaction() != null && !sessionHolder.getTransaction().wasRolledBack()) {
                    sessionHolder.getTransaction().commit();
                }
            }

            private void openSession() {
                SessionFactory sessionFactory = FinderDispatcherGenericDaoImpl.this.getSessionFactory();
                Session session = SessionFactoryUtils.getSession(sessionFactory, true);
                SessionHolder holder = new SessionHolder(session);
                session.setFlushMode(FlushMode.AUTO);
                TransactionSynchronizationManager.bindResource(sessionFactory, holder);
            }
        };
    }

    @SuppressWarnings(value = "unchecked")
    public Class getObjectType() {
        return daoInterface;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object getServicesImpl() {
        return servicesImpl;
    }

    public void setServicesImpl(Object servicesImpl) {
        this.servicesImpl = servicesImpl;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    @Required
    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getDaoInterface() {
        return daoInterface;
    }

    @Required
    public void setDaoInterface(Class<?> daoInterface) {
        this.daoInterface = daoInterface;
    }

    @Required
    public void setUsernameResolver(UsernameResolver usernameResolver) {
        this.usernameResolver = usernameResolver;
    }
}
