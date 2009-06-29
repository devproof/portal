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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.module.common.annotation.BeanJoin;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author Carsten Hufe
 */
public class DataProviderDaoImpl<T> extends HibernateDaoSupport implements DataProviderDao<T> {

	@Override
	@SuppressWarnings("unchecked")
	public T findById(final Class<T> clazz, final Serializable id) {
		return (T) this.getSession().get(clazz, id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(final Class<T> clazz) {
		return this.getSession().createQuery("Select distinct(e) from " + clazz.getSimpleName() + " e").list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll(final Class<T> clazz, final int first, final int count) {
		Query q = this.getSession().createQuery("Select distinct(e) from " + clazz.getSimpleName() + " e");
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.list();
	}

	private Query createHibernateQuery(final String target, final Class<T> clazz, final String sortParam, final boolean ascending, final Serializable beanQuery, final List<String> prefetch) {
		StringBuilder buf = new StringBuilder();
		buf.append("Select ").append(target).append(" from ").append(clazz.getSimpleName()).append(" e ");

		if (prefetch != null) {
			for (String preStr : prefetch) {
				buf.append(" left join fetch e.").append(preStr).append(" ");
			}
		}

		List<Object> values = new ArrayList<Object>();
		if (beanQuery != null) {
			if (beanQuery.getClass().isAnnotationPresent(BeanJoin.class)) {
				BeanJoin join = beanQuery.getClass().getAnnotation(BeanJoin.class);
				buf.append(" ").append(join.value()).append(" ");
			}

			Method methods[] = beanQuery.getClass().getMethods();
			if (methods.length > 0) {
				boolean firstClause = true;
				for (Method method : methods) {
					if (method.getName().startsWith("get") && method.isAnnotationPresent(BeanQuery.class)) {
						Object value = null;
						try {
							value = method.invoke(beanQuery);

						} catch (Exception e) {
							throw new UnhandledException(e);
						}
						if (value != null) {
							if (firstClause) {
								firstClause = false;
								buf.append(" where ");
							} else {
								buf.append(" and ");
							}
							BeanQuery bean = method.getAnnotation(BeanQuery.class);
							buf.append(bean.value());
							int countMatches = StringUtils.countMatches(bean.value(), "?");
							for (int j = 0; j < countMatches; j++) {
								values.add(value);
							}
						}
					}
				}
			}
		}

		if (sortParam != null) {
			buf.append(" order by e.").append(sortParam).append(" ").append((ascending ? "ASC" : "DESC"));
		}

		Query q = this.getSession().createQuery(buf.toString());
		Object valArray[] = values.toArray();
		for (int i = 0; i < valArray.length; i++) {
			q.setParameter(i, valArray[i]);
		}
		return q;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAllWithQuery(final Class<T> clazz, final String sortParam, final boolean ascending, final int first, final int count, final Serializable beanQuery, final List<String> prefetch) {
		Query q = this.createHibernateQuery("distinct(e)", clazz, sortParam, ascending, beanQuery, prefetch);
		q.setFirstResult(first);
		q.setMaxResults(count);
		return q.list();
	}

	@Override
	public int getSize(final Class<T> clazz, final Serializable beanQuery) {
		// count(distinct e)
		return this.getSize(clazz, "count(distinct e)", beanQuery);
	}

	@Override
	public int getSize(final Class<T> clazz, final String countQuery, final Serializable beanQuery) {
		Long count = (Long) this.createHibernateQuery(countQuery, clazz, null, false, beanQuery, null).uniqueResult();
		return count.intValue();
	}
}