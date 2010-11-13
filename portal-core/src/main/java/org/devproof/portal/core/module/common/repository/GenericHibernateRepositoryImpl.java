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

import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.Modification;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.historization.interceptor.Historize;
import org.devproof.portal.core.module.historization.interceptor.Historizer;
import org.devproof.portal.core.module.user.service.UsernameResolver;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

/**
 * Implementation of the generic dao
 *
 * @author Carsten Hufe
 * @param <T>
 * entity type
 * @param <PK>
 * primary key type
 */
public class GenericHibernateRepositoryImpl<T, PK extends Serializable> extends HibernateDaoSupport implements CrudRepository<T, PK> {
    private UsernameResolver usernameResolver;
    private Class<T> type;
    private ApplicationContext applicationContext;

    public GenericHibernateRepositoryImpl(Class<T> type) {
        this.type = type;
        logger.debug("Constructor GenericHibernateRepositoryImpl");
    }

    @SuppressWarnings(value = "unchecked")
    public T findById(PK id) {
        return (T) getSession().get(type, id);
    }

    @SuppressWarnings("unchecked")
    public T save(T entity) {
        logger.debug("save " + type);
        updateModificationData(entity);
//        return (T) getSession().merge(entity);
        getSession().saveOrUpdate(entity);
        // TODO change return type
        return null;
    }

    private void updateModificationData(T entity) {
        if (entity instanceof Modification) {
            Modification base = (Modification) entity;
            // only works in the request
            if (base.isUpdateModificationData()) {
                String username = usernameResolver.getUsername();
                logger.debug("Modification " + entity + "set creation date and user");
                if (base.getCreatedAt() == null) {
                    base.setCreatedAt(PortalUtil.now());
                }
                if (base.getCreatedBy() == null) {
                    base.setCreatedBy(username);
                }
                base.setModifiedAt(PortalUtil.now());
                base.setModifiedBy(username);
            }
        }
    }

    @Override
    public void refresh(T entity) {
        getSession().refresh(entity);
    }

    public void delete(T entity) throws DeleteFailedException {
        getSession().delete(entity);
    }

    public Object executeFinder(String query, Object[] queryArgs, Method method, Integer firstResults, Integer maxResults) {
        String hqlQuery = replaceGenericTypeName(query);
        Query q = getSession().createQuery(hqlQuery);
        setCacheConfiguration(method, q);
        setParameter(queryArgs, q);
        setResultLimitations(firstResults, maxResults, q);
        if (Collection.class.isAssignableFrom(method.getReturnType())) {
            return q.list();
        } else {
            return q.uniqueResult();
        }
    }

    private void setCacheConfiguration(Method method, Query q) {
        CacheQuery cacheAnnotation = method.getAnnotation(CacheQuery.class);
        if (cacheAnnotation != null) {
            handleCacheConfiguration(q, cacheAnnotation);
        } else {
            cacheAnnotation = method.getDeclaringClass().getAnnotation(CacheQuery.class);
            if (cacheAnnotation != null) {
                handleCacheConfiguration(q, cacheAnnotation);
            }
        }
    }

    private void handleCacheConfiguration(Query q, CacheQuery cacheAnnotation) {
        q.setCacheable(cacheAnnotation.enabled());
        if (!"".equals(cacheAnnotation.region())) {
            q.setCacheMode(CacheMode.parse(cacheAnnotation.cacheMode()));
        }
        if (!"".equals(cacheAnnotation.region())) {
            q.setCacheRegion(cacheAnnotation.region());
        }
    }

    private void setResultLimitations(Integer firstResults, Integer maxResults, Query q) {
        if (firstResults != null) {
            q.setFirstResult(firstResults);
        }
        if (maxResults != null) {
            q.setMaxResults(maxResults);
        }
    }

    public void executeUpdate(String query, Object[] queryArgs) {
        String hqlQuery = replaceGenericTypeName(query);
        Query q = getSession().createQuery(hqlQuery);
        setParameter(queryArgs, q);
        q.executeUpdate();
    }

    private String replaceGenericTypeName(String query) {
        if (query.contains("$TYPE")) {
            return query.replace("$TYPE", type.getSimpleName());
        }
        return query;
    }

    private void setParameter(Object[] queryArgs, Query q) {
        if (queryArgs != null) {
            for (int i = 0; i < queryArgs.length; i++) {
                q.setParameter(i, queryArgs[i]);
            }
        }
    }

    public Class<T> getType() {
        return type;
    }

    @Required
    public void setType(Class<T> type) {
        this.type = type;
    }

    @Required
    public void setUsernameResolver(UsernameResolver usernameResolver) {
        this.usernameResolver = usernameResolver;
    }

    @Required
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
