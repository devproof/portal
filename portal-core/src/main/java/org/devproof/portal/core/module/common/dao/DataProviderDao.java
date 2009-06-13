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
import java.util.List;

/**
 * Dao interface for a generic wicket data provider
 * 
 * @author Carsten Hufe
 */
public interface DataProviderDao<T> {

	/**
	 * find all entities with limits and query params
	 * 
	 * @param clazz
	 *            Class to search
	 * @param sortParam
	 *            sort parameter
	 * @param ascending
	 *            ascending
	 * @param first
	 *            first row
	 * @param count
	 *            number of results
	 * @param beanQuery
	 *            bean with criterias (use BeanQuery and BeanJoin annonation)
	 * @return list with results
	 */
	public List<T> findAllWithQuery(Class<T> clazz, String sortParam, boolean ascending, int first, int count, Serializable beanQuery, List<String> prefetch);

	/**
	 * find by Id
	 * 
	 * @param clazz
	 *            entity class
	 * @param id
	 *            id of entity
	 * @return entity
	 */
	public T findById(Class<T> clazz, Serializable id);

	/**
	 * finds everything
	 * 
	 * @param clazz
	 *            entity class
	 * @return list with all entities
	 */
	public List<T> findAll(Class<T> clazz);

	/**
	 * find all entities with limits
	 * 
	 * @param clazz
	 *            entity class
	 * @param first
	 *            first row
	 * @param count
	 *            number of following rows
	 * @return all entites limitated by first and count
	 */
	public List<T> findAll(Class<T> clazz, int first, int count);

	/**
	 * Returns the number of rows
	 * 
	 * @param clazz
	 *            entity clas
	 * @param beanQuery
	 *            bean with criterias (use BeanQuery and BeanJoin annonation)
	 * @return number of results
	 */
	public int getSize(Class<T> clazz, Serializable beanQuery);

	/**
	 * Returns the number of rows
	 * 
	 * @param clazz
	 *            entity clas
	 * @param beanQuery
	 *            bean with criterias (use BeanQuery and BeanJoin annonation)
	 * @param countQuery
	 *            specific count query e.g. "count(distinct e.name)"
	 * @return number of results
	 */
	public int getSize(Class<T> clazz, String countQuery, Serializable beanQuery);
}