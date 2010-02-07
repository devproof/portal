/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
package org.devproof.portal.core.module.common.service;

import java.io.Serializable;
import java.util.List;

/**
 * Default CRUD methods
 * 
 * @author Carsten Hufe
 */
public interface CrudService<T, PK extends Serializable> {
	/**
	 * Get an entity by id
	 * 
	 * @param id
	 *            primary key
	 * @return entity matching the primary key
	 */
	T findById(PK id);

	/**
	 * Find all entites
	 * 
	 * @return returns all entities
	 */
	List<T> findAll();

	/**
	 * Stores/updates an entity
	 * 
	 * @param entity
	 *            Entity to save
	 */
	void save(T entity);

	/**
	 * Deletes an entity
	 * 
	 * @param entity
	 *            Entity to delete
	 */
	void delete(T entity);
}
