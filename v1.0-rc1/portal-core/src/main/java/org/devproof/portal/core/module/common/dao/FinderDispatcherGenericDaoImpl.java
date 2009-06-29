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
package org.devproof.portal.core.module.common.dao;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * The executing class for the generic dao:
 * 
 * 1. executes the generic dao methods 2. executes generic queries 3. executes
 * generic bulk updates 4. executes own implemented dao methods (from
 * servicesImpl) if no Query or BulkUpdate annotation exist 5. opens transaction
 * on the first change call (save, update, merge, delete, bulkupdate) 6. opens a
 * session if no ones exists and closes it after the call
 * 
 * 
 * @author Carsten Hufe
 * 
 * @param <T>
 *            entity type
 * @param <PK>
 *            primary key type
 */
public class FinderDispatcherGenericDaoImpl<T, PK extends Serializable> extends HibernateDaoSupport implements FactoryBean, Serializable {

	private static final long serialVersionUID = -3752572093862325307L;

	private Object servicesImpl;
	private Class<T> entityClass;
	private Class<GenericDao<T, PK>> daoInterface;

	public Object getObject() throws Exception {
		ProxyFactory result = new ProxyFactory();
		GenericHibernateDaoImpl<T, PK> genericDao = new GenericHibernateDaoImpl<T, PK>(this.entityClass);
		genericDao.setSessionFactory(getSessionFactory());
		genericDao.setHibernateTemplate(getHibernateTemplate());
		result.setTarget(genericDao);
		result.setInterfaces(new Class[] { this.daoInterface });
		result.addAdvice(new MethodInterceptor() {
			public Object invoke(final MethodInvocation invocation) throws Throwable {
				Object result = null;
				/*
				 * If the session is opened at this place, the same method will
				 * close the session and transaction
				 */
				boolean hasSession = TransactionSynchronizationManager.hasResource(FinderDispatcherGenericDaoImpl.this.getSessionFactory());
				if (!hasSession) {
					Session session = SessionFactoryUtils.getSession(FinderDispatcherGenericDaoImpl.this.getSessionFactory(), true);
					SessionHolder holder = new SessionHolder(session);
					session.setFlushMode(FlushMode.AUTO);
					TransactionSynchronizationManager.bindResource(FinderDispatcherGenericDaoImpl.this.getSessionFactory(), holder);
				}

				Method method = invocation.getMethod();
				if (method.isAnnotationPresent(Query.class)) {
					Query query = method.getAnnotation(Query.class);
					FinderExecutor target = (FinderExecutor) invocation.getThis();

					if (query.limitClause()) {
						Object orginal[] = invocation.getArguments();
						int len = orginal.length - 2;
						Object copy[] = new Object[len];
						for (int i = 0; i < len; i++) {
							copy[i] = orginal[i];
						}
						result = target.executeFinder(query.value(), copy, method.getReturnType(), (Integer) orginal[len], (Integer) orginal[len + 1]);
					} else {
						result = target.executeFinder(query.value(), invocation.getArguments(), method.getReturnType(), null, null);
					}
				} else if (method.isAnnotationPresent(BulkUpdate.class)) {
					SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(FinderDispatcherGenericDaoImpl.this.getSessionFactory());
					if (holder.getTransaction() == null) {
						holder.setTransaction(holder.getSession().beginTransaction());
					}
					BulkUpdate bulkUpdate = method.getAnnotation(BulkUpdate.class);
					FinderExecutor target = (FinderExecutor) invocation.getThis();
					target.executeUpdate(bulkUpdate.value(), invocation.getArguments());
				} else {

					Method serviceMethod = FinderDispatcherGenericDaoImpl.this.servicesImpl != null ? FinderDispatcherGenericDaoImpl.this.servicesImpl.getClass().getMethod(
							invocation.getMethod().getName(), invocation.getMethod().getParameterTypes()) : null;
					if (serviceMethod != null) {
						result = serviceMethod.invoke(FinderDispatcherGenericDaoImpl.this.servicesImpl, invocation.getArguments());
					} else {
						// should be only save, update, delete from the generic
						// dao
						result = invocation.proceed();
					}
				}
				if (!hasSession) {
					SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(FinderDispatcherGenericDaoImpl.this.getSessionFactory());
					if (sessionHolder.getTransaction() != null && !sessionHolder.getTransaction().wasRolledBack()) {
						sessionHolder.getTransaction().commit();
					}
					SessionFactoryUtils.closeSession(sessionHolder.getSession());
				}
				return result;

			}
		});
		return result.getProxy();
	}

	@SuppressWarnings(value = "unchecked")
	public Class getObjectType() {
		return this.daoInterface;
	}

	public boolean isSingleton() {
		return true;
	}

	public Object getServicesImpl() {
		return this.servicesImpl;
	}

	public void setServicesImpl(final Object servicesImpl) {
		this.servicesImpl = servicesImpl;
	}

	public Class<T> getEntityClass() {
		return this.entityClass;
	}

	public void setEntityClass(final Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Class<GenericDao<T, PK>> getDaoInterface() {
		return this.daoInterface;
	}

	public void setDaoInterface(final Class<GenericDao<T, PK>> daoInterface) {
		this.daoInterface = daoInterface;
	}
}
