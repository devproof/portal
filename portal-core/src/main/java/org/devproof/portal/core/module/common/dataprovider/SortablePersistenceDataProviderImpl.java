/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.common.dataprovider;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.devproof.portal.core.module.common.dao.DataProviderDao;

/**
 * Generic data provider for wicket data views Only set the type and you will
 * have a persistencinated data provider
 * 
 * @author Carsten Hufe
 * 
 * @param <T>
 *            Entity type
 */
public class SortablePersistenceDataProviderImpl<T extends Serializable> extends SortableDataProvider<T> implements
		SortableQueryDataProvider<T> {
	private static final long serialVersionUID = 1L;

	private Class<T> entityClass;
	private DataProviderDao<T> dataProviderDao;
	private Serializable queryObject;
	private List<String> prefetch;
	private String countQuery;

	@Override
	public Iterator<? extends T> iterator(int first, int count) {
		SortParam sp = getSort();
		List<T> list = dataProviderDao.findAllWithQuery(entityClass, sp.getProperty(), sp.isAscending(), first, count,
				queryObject, prefetch);
		return list.iterator();
	}

	public IModel<T> model(T obj) {
		// return new CompoundPropertyModel<T>(obj);
		return Model.of(obj);
	}

	@Override
	public int size() {
		if (countQuery != null) {
			return dataProviderDao.getSize(entityClass, countQuery, queryObject);
		} else {
			return dataProviderDao.getSize(entityClass, queryObject);
		}
	}

	@Override
	public Serializable getQueryObject() {
		return queryObject;
	}

	@Override
	public void setQueryObject(Serializable queryObject) {
		this.queryObject = queryObject;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public void setDataProviderDao(DataProviderDao<T> dataProviderDao) {
		this.dataProviderDao = dataProviderDao;
	}

	public void setPrefetch(List<String> prefetch) {
		this.prefetch = prefetch;
	}

	public void setCountQuery(String countQuery) {
		this.countQuery = countQuery;
	}
}
