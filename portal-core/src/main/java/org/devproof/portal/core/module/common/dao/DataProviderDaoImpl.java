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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.module.common.annotation.BeanJoin;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.hibernate.CacheMode;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class DataProviderDaoImpl<T> extends HibernateDaoSupport implements DataProviderDao<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T findById(Class<T> clazz, Serializable id) {
		return (T) this.getSession().get(clazz, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(Class<T> clazz) {
		Query q = getSession().createQuery("Select distinct(e) from " + clazz.getSimpleName() + " e");
		setCacheConfiguration(q, clazz);
		return q.list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(Class<T> clazz, int first, int count) {
		Query q = this.getSession().createQuery("Select distinct(e) from " + clazz.getSimpleName() + " e");
		setCacheConfiguration(q, clazz);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.list();
	}

	private Query createHibernateQuery(String target, Class<T> clazz, String sortParam, boolean ascending,
			Serializable beanQuery, List<String> prefetch) {
		StringBuilder hqlQuery = new StringBuilder();
		List<Object> queryParameter = new ArrayList<Object>();
		hqlQuery.append(createSelectFrom(target, clazz));
		hqlQuery.append(createPrefetch(prefetch));
		hqlQuery.append(createWhereConditions(beanQuery, queryParameter));
		hqlQuery.append(createOrderBy(sortParam, ascending));
		return createHibernateQuery(hqlQuery.toString(), queryParameter);
	}

	private StringBuilder createWhereConditions(Serializable beanQuery, List<Object> queryParameter) {
		StringBuilder hqlQuery = new StringBuilder();
		if (beanQuery != null) {
			appendTableJoin(beanQuery, hqlQuery);
			Method methods[] = beanQuery.getClass().getMethods();
			if (methods.length > 0) {
				boolean firstClause = true;
				for (Method method : methods) {
					if (isGetterWithBeanQuery(method)) {
						Object value = invokeGetter(beanQuery, method);
						if (value != null) {
							if (firstClause) {
								firstClause = false;
								hqlQuery.append(" where ");
							} else {
								hqlQuery.append(" and ");
							}
							BeanQuery bean = method.getAnnotation(BeanQuery.class);
							hqlQuery.append(bean.value());
							int countMatches = StringUtils.countMatches(bean.value(), "?");
							for (int j = 0; j < countMatches; j++) {
								queryParameter.add(value);
							}
						}
					}
				}
			}
		}
		return hqlQuery;
	}

	private Object invokeGetter(Serializable beanQuery, Method method) {
		Object value = null;
		try {
			value = method.invoke(beanQuery);

		} catch (Exception e) {
			throw new UnhandledException(e);
		}
		return value;
	}

	private void appendTableJoin(Serializable beanQuery, StringBuilder hqlQuery) {
		if (beanQuery.getClass().isAnnotationPresent(BeanJoin.class)) {
			BeanJoin join = beanQuery.getClass().getAnnotation(BeanJoin.class);
			hqlQuery.append(" ").append(join.value()).append(" ");
		}
	}

	private boolean isGetterWithBeanQuery(Method method) {
		return method.getName().startsWith("get") && method.isAnnotationPresent(BeanQuery.class);
	}

	private StringBuilder createOrderBy(String sortParam, boolean ascending) {
		StringBuilder hqlQuery = new StringBuilder();
		if (sortParam != null) {
			hqlQuery.append(" order by e.").append(sortParam).append(" ").append((ascending ? "ASC" : "DESC"));
		}
		return hqlQuery;
	}

	private StringBuilder createPrefetch(List<String> prefetch) {
		StringBuilder hqlQuery = new StringBuilder();
		if (prefetch != null) {
			for (String preStr : prefetch) {
				hqlQuery.append(" left join fetch e.").append(preStr).append(" ");
			}
		}
		return hqlQuery;
	}

	private StringBuilder createSelectFrom(String target, Class<T> clazz) {
		StringBuilder hqlQuery = new StringBuilder();
		hqlQuery.append("Select ").append(target).append(" from ").append(clazz.getSimpleName()).append(" e ");
		return hqlQuery;
	}

	private Query createHibernateQuery(String query, List<Object> queryParameter) {
		Query q = getSession().createQuery(query);
		Object valArray[] = queryParameter.toArray();
		for (int i = 0; i < valArray.length; i++) {
			q.setParameter(i, valArray[i]);
		}
		return q;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllWithQuery(Class<T> clazz, String sortParam, boolean ascending, int first, int count,
			Serializable beanQuery, List<String> prefetch) {
		Query q = createHibernateQuery("distinct(e)", clazz, sortParam, ascending, beanQuery, prefetch);
		q.setFirstResult(first);
		q.setMaxResults(count);
		setCacheConfiguration(q, clazz);
		return q.list();
	}

	@Override
	public int getSize(Class<T> clazz, Serializable beanQuery) {
		return getSize(clazz, "count(distinct e)", beanQuery);
	}

	@Override
	public int getSize(Class<T> clazz, String countQuery, Serializable beanQuery) {
		Long count = (Long) createHibernateQuery(countQuery, clazz, null, false, beanQuery, null).uniqueResult();
		return count.intValue();
	}

	private void setCacheConfiguration(Query q, Class<?> clazz) {
		CacheQuery cacheAnnotation = clazz.getAnnotation(CacheQuery.class);
		if (cacheAnnotation != null) {
			handleCacheConfiguration(q, cacheAnnotation);
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
}