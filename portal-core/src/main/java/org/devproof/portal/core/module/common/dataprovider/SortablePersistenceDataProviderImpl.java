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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.dao.DataProviderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

/**
 * Generic data provider for wicket data views Only set the type and you will
 * have a persistencinated data provider
 *
 * @author Carsten Hufe
 * @param <T>
 * Entity type
 */
public class SortablePersistenceDataProviderImpl<T extends Serializable, SQ extends Serializable> extends SortableDataProvider<T> implements SortableQueryDataProvider<T, SQ> {
    private static final long serialVersionUID = 1L;

    private Class<T> entityClass;
    private Class<T> queryClass;
    private DataProviderDao<T> dataProviderDao;
    private IModel<SQ> searchQueryModel;
    private List<String> prefetch;
    private String countQuery;

    @Override
    public Iterator<? extends T> iterator(int first, int count) {
        SortParam sp = getSort();
        SQ searchQuery = getSearchQueryModel().getObject();
        List<T> list = dataProviderDao.findAllWithQuery(entityClass, sp.getProperty(), sp.isAscending(), first, count, searchQuery, prefetch);
        return list.iterator();
    }

    public IModel<T> model(T obj) {
        return Model.of(obj);
    }

    @Override
    public int size() {
        SQ searchQuery = getSearchQueryModel().getObject();
        if (StringUtils.isNotBlank(countQuery)) {
            return dataProviderDao.getSize(entityClass, countQuery, searchQuery);
        } else {
            return dataProviderDao.getSize(entityClass, searchQuery);
        }
    }

    @Override
    public IModel<SQ> getSearchQueryModel() {
        if (searchQueryModel == null) {
            searchQueryModel = new Model<SQ>();
            if (!queryClass.isAssignableFrom(RegisterGenericDataProvider.NO_QUERY.class)) {
                try {
                    @SuppressWarnings("unchecked") SQ query = (SQ) queryClass.newInstance();
                    searchQueryModel.setObject(query);
                } catch (InstantiationException e) {
                    throw new UnhandledException(e);
                } catch (IllegalAccessException e) {
                    throw new UnhandledException(e);
                }
            }
        }
        return searchQueryModel;
    }

    public void setEntityClass(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Autowired
    public void setDataProviderDao(DataProviderDao<T> dataProviderDao) {
        this.dataProviderDao = dataProviderDao;
    }

    public void setPrefetch(List<String> prefetch) {
        this.prefetch = prefetch;
    }

    public void setCountQuery(String countQuery) {
        this.countQuery = countQuery;
    }

    @Required
    public void setQueryClass(Class<T> queryClass) {
        this.queryClass = queryClass;
    }
}
