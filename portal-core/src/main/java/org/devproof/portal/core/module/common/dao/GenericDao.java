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

import java.io.Serializable;

/**
 * GenericDao interface with CRUD methods
 * 
 * @author Carsten Hufe
 */
public interface GenericDao<T, PK extends Serializable> extends FinderExecutor {
	/**
	 * Get an entity by id
	 * 
	 * @param id
	 * @return entity
	 */
	T findById(PK id);

	// /**
	// * Find all entites
	// *
	// * @return list of entities
	// */
	// List<T> findAll();

	/**
	 * Stores an entity
	 * 
	 * @param entity
	 *            entity to save
	 */
	T save(T entity);

	/**
	 * Deletes an entity
	 * 
	 * @param entity
	 *            entity to delete
	 */
	void delete(T entity);

	/**
	 * refresh an entity
	 * 
	 * @param entity
	 *            entity to refresh
	 */
	void refresh(T entity);

	/**
	 * Returns the represented generic type
	 */
	Class<T> getType();
}
