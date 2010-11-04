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
package org.devproof.portal.core.module.tag.dao;

import java.util.List;

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.GenericRepository;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.TagConstants;

/**
 * @author Carsten Hufe
 */
@CacheQuery(region = TagConstants.QUERY_CACHE_REGION)
public interface TagRepository<T> extends GenericRepository<T, String> {
    @Query("select t from $TYPE t where t.tagname like ?||'%'")
    List<T> findTagsStartingWith(String prefix);

    @BulkUpdate("delete from $TYPE t where size(t.referencedObjects) = 0")
    void deleteUnusedTags();

    @Query(value = "select t from $TYPE t order by size(t.referencedObjects) desc", limitClause = true)
    List<T> findMostPopularTags(Integer firstResult, Integer maxResult);

    @Query(value = "select t from $TYPE t where exists(from $TYPE et join et.referencedObjects ro left join ro.allRights ar "
			+ "where ar in(select r from RightEntity r join r.roles rt where rt = ? and r.right like ?||'%') and t = et) " +
    			"order by size(t.referencedObjects) desc", limitClause = true)
    List<T> findMostPopularTags(RoleEntity role, String viewRight, Integer firstResult, Integer maxResult);
}
