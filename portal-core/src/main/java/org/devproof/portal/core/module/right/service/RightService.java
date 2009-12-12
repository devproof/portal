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
package org.devproof.portal.core.module.right.service;

import java.util.List;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.right.entity.RightEntity;

/**
 * Manage user rights
 * 
 * @author Carsten Hufe
 */
public interface RightService extends CrudService<RightEntity, String> {
	/**
	 * Returns the dirty time
	 * 
	 * @return last refreshed unix time
	 */
	public long getDirtyTime();

	/**
	 * Refreshes the global instanz of all rights
	 */
	public void refreshGlobalApplicationRights();

	/**
	 * Returns all rights from member variable
	 * 
	 * @return list with all rights
	 */
	public List<RightEntity> getAllRights();

	/**
	 * Returns all rights starting with the given prefix
	 * 
	 * @param prefix
	 *            right prefix
	 * @return list with matching rights
	 */
	public List<RightEntity> findRightsStartingWith(String prefix);

	/**
	 * Returns all rights ordered by description
	 * 
	 * @return list with rights
	 */
	public List<RightEntity> findAllOrderByDescription();

	/**
	 * Returns a new instance of RightEntity
	 * 
	 * @return new instance of RightEntity
	 */
	public RightEntity newRightEntity();

	/**
	 * Returns a new instance of RightEntity
	 * 
	 * @param right
	 *            right key
	 * @return new instance of RightEntity
	 */
	public RightEntity newRightEntity(String right);
}
