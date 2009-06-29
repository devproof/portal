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
package org.devproof.portal.core.module.tag.service;

import java.util.List;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.role.entity.RoleEntity;

/**
 * @author Carsten Hufe
 */
public interface TagService<T> extends CrudService<T, String> {
	/**
	 * Returns all tags starting with the given string
	 */
	public List<T> findTagsStartingWith(String prefix);

	/**
	 * Deletes all unused/unreferenced tags
	 */
	public void deleteUnusedTags();

	/**
	 * Returns the most popular blog tags
	 */
	public List<T> findMostPopularTags(Integer firstResult, Integer maxResult);

	/**
	 * Returns the most popular blog tags filtered by role
	 */
	public List<T> findMostPopularTags(RoleEntity role, Integer firstResult, Integer maxResult);

	/**
	 * Returns a new instance of the tag
	 */
	public T newTagEntity(String tag);

	/**
	 * Returns the belonging tag right for the related object type
	 */
	public String getRelatedTagRight();
}
