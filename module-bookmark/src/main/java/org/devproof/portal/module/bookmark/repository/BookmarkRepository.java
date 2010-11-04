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
package org.devproof.portal.module.bookmark.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.entity.Bookmark.Source;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@GenericRepository("bookmarkRepository")
@CacheQuery(region = BookmarkConstants.QUERY_CACHE_REGION)
public interface BookmarkRepository extends GenericDao<Bookmark, Integer> {
    @Query("Select b from Bookmark b")
    List<Bookmark> findAll();

    @CacheQuery(enabled = false)
    @Query("select b.allRights from Bookmark b where b.modifiedAt = (select max(modifiedAt) from Bookmark)")
    List<RightEntity> findLastSelectedRights();

    @Query(value = "select b from Bookmark b where exists(from Bookmark eb left join eb.allRights ar "
			+ "where ar in(select r from RightEntity r join r.roles rt where rt = ? and r.right like 'bookmark.view%') and b = eb)" +
					" order by b.modifiedAt desc", limitClause = true)
    List<Bookmark> findAllBookmarksForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult, Integer maxResult);

    @Query("select b from Bookmark b where b.source = ?")
    List<Bookmark> findBookmarksBySource(Source source);

    @BulkUpdate("update Bookmark b set b.hits = (b.hits + 1) where b = ?")
    void incrementHits(Bookmark bookmark);

    @BulkUpdate("update Bookmark b set b.numberOfVotes = (b.numberOfVotes + 1), b.sumOfRating = (b.sumOfRating + ?) where b = ?")
    void rateBookmark(Integer rating, Bookmark bookmark);

    @BulkUpdate("update Bookmark b set b.broken = true where b = ?")
    void markBrokenBookmark(Bookmark bookmark);

    @BulkUpdate("update Bookmark b set b.broken = false where b = ?")
    void markValidBookmark(Bookmark bookmark);
}
