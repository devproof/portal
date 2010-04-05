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
package org.devproof.portal.module.bookmark.dao;

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@CacheQuery(region = BookmarkConstants.QUERY_CACHE_REGION)
public interface BookmarkDao extends GenericDao<BookmarkEntity, Integer> {
    @Query("Select distinct(b) from BookmarkEntity b")
    List<BookmarkEntity> findAll();

    @CacheQuery(enabled = false)
    @Query("select b.allRights from BookmarkEntity b where b.modifiedAt = (select max(modifiedAt) from BookmarkEntity)")
    List<RightEntity> findLastSelectedRights();

    @Query(value = "select distinct(b) from BookmarkEntity b join b.allRights vr" + " where vr in (select rt from RoleEntity r join r.rights rt where r = ? and rt.right like 'bookmark.view%') order by b.modifiedAt desc", limitClause = true)
    List<BookmarkEntity> findAllBookmarksForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult, Integer maxResult);

    @Query("select distinct b from BookmarkEntity b left join fetch b.tags where b.source = ?")
    List<BookmarkEntity> findBookmarksBySource(final Source source);

    @BulkUpdate("update BookmarkEntity b set b.hits = (b.hits + 1) where b = ?")
    void incrementHits(BookmarkEntity bookmark);

    @BulkUpdate("update BookmarkEntity b set b.numberOfVotes = (b.numberOfVotes + 1), b.sumOfRating = (b.sumOfRating + ?) where b = ?")
    void rateBookmark(Integer rating, BookmarkEntity bookmark);

    @BulkUpdate("update BookmarkEntity b set b.broken = true where b = ?")
    void markBrokenBookmark(BookmarkEntity bookmark);

    @BulkUpdate("update BookmarkEntity b set b.broken = false where b = ?")
    void markValidBookmark(BookmarkEntity bookmark);
}
