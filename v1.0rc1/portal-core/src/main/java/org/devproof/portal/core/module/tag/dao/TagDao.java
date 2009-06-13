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
package org.devproof.portal.core.module.tag.dao;

import java.util.List;

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.role.entity.RoleEntity;

/**
 * @author Carsten Hufe
 */
public interface TagDao<T> extends GenericDao<T, String> {
	@Query("select distinct(t) from $TYPE t where t.tagname like ?||'%'")
	public List<T> findTagsStartingWith(String prefix);

	@BulkUpdate("delete from $TYPE t where size(t.referencedObjects) = 0")
	public void deleteUnusedTags();

	@Query(value = "select distinct(t) from $TYPE t order by size(t.referencedObjects) desc", limitClause = true)
	public List<T> findMostPopularTags(Integer firstResult, Integer maxResult);

	@Query(value = "select distinct(t) from $TYPE t join t.referencedObjects ro join ro.allRights ar where "
			+ "ar in (select rt from RoleEntity r join r.rights rt where r = ? and rt.right like ?||'%') order by size(t.referencedObjects) desc", limitClause = true)
	public List<T> findMostPopularTags(RoleEntity role, String viewRight, Integer firstResult, Integer maxResult);
}
