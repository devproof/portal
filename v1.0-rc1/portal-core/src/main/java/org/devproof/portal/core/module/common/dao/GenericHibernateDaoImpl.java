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
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Implementation of the generic dao
 * 
 * @author Carsten Hufe
 * 
 * @param <T>
 *            entity type
 * @param <PK>
 *            primary key type
 */
public class GenericHibernateDaoImpl<T, PK extends Serializable> extends HibernateDaoSupport implements GenericDao<T, PK> {
	private static final Log LOG = LogFactory.getLog(GenericHibernateDaoImpl.class);

	private Class<T> type;

	public GenericHibernateDaoImpl(final Class<T> type) {
		this.type = type;
		LOG.debug("Constructor GenericHibernateDaoImpl");
	}

	@SuppressWarnings(value = "unchecked")
	public T findById(final PK id) {
		return (T) this.getSession().get(this.type, id);
	}

	@SuppressWarnings(value = "unchecked")
	public List<T> findAll() {
		return this.getSession().createQuery("Select distinct(e) from " + this.type.getSimpleName() + " e").list();
	}

	public void save(final T entity) {
		final SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());
		if (holder.getTransaction() == null) {
			holder.setTransaction(holder.getSession().beginTransaction());
		}
		LOG.debug("save " + this.type);
		// FIXME Sorry a little bit crucial stuff! No wicket stuff in the
		// DAO!!!!

		if (entity instanceof BaseEntity) {
			final BaseEntity base = (BaseEntity) entity;
			// only works in the request
			if (RequestCycle.get() != null) {
				LOG.debug("BaseEntity " + entity + "set creation date and user");
				final PortalSession session = ((PortalSession) org.apache.wicket.Session.get());
				if (base.getCreatedAt() == null) {
					base.setCreatedAt(PortalUtil.now());
				}
				if (base.getCreatedBy() == null) {
					base.setCreatedBy(session.getUser().getUsername());
				}
				base.setModifiedAt(PortalUtil.now());
				base.setModifiedBy(session.getUser().getUsername());
			}
		}
		this.getSession().merge(entity);
	}

	@Override
	public void refresh(final T entity) {
		this.getSession().refresh(entity);
	}

	public void delete(final T entity) {
		final SessionHolder holder = (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());
		if (holder.getTransaction() == null) {
			LOG.debug("No transaction found, start one.");
			holder.setTransaction(holder.getSession().beginTransaction());
		}
		this.getSession().delete(entity);
	}

	public Object executeFinder(final String query, final Object[] queryArgs, final Class<?> returnType, final Integer firstResults, final Integer maxResults) {
		String tmpQuery = query;
		if (query.contains("$TYPE")) {
			tmpQuery = tmpQuery.replace("$TYPE", this.type.getSimpleName());
		}
		final Query q = this.getSession().createQuery(tmpQuery);
		if (queryArgs != null) {
			for (int i = 0; i < queryArgs.length; i++) {
				q.setParameter(i, queryArgs[i]);
			}
		}
		if (firstResults != null) {
			q.setFirstResult(firstResults);
		}
		if (maxResults != null) {
			q.setMaxResults(maxResults);
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			return q.list();
		} else {
			return q.uniqueResult();
		}
	}

	public void executeUpdate(final String query, final Object[] queryArgs) {
		String tmpQuery = query;
		if (query.contains("$TYPE")) {
			tmpQuery = tmpQuery.replace("$TYPE", this.type.getSimpleName());
		}
		final Query q = this.getSession().createQuery(tmpQuery);
		if (queryArgs != null) {
			for (int i = 0; i < queryArgs.length; i++) {
				q.setParameter(i, queryArgs[i]);
			}
		}
		q.executeUpdate();
	}

	public Class<T> getType() {
		return this.type;
	}

	public void setType(final Class<T> type) {
		this.type = type;
	}
}
