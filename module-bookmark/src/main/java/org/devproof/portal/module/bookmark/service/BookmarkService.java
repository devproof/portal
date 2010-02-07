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
package org.devproof.portal.module.bookmark.service;

import java.util.List;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;

/**
 * @author Carsten Hufe
 */
public interface BookmarkService extends CrudService<BookmarkEntity, Integer> {
	/**
	 * Returns a new instance of BookmarkEntity
	 * 
	 * @return new instance of {@link BookmarkEntity}
	 */
	BookmarkEntity newBookmarkEntity();

	/**
	 * Returns all bookmarks which are available for the given role
	 * 
	 * @param role
	 *            role enity
	 * @param firstResult
	 *            first result
	 * @param maxResult
	 *            maximum result
	 * @return list with bookmarks
	 */
	List<BookmarkEntity> findAllBookmarksForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult,
			Integer maxResult);

	/**
	 * Returns all bookmarks with the given source
	 * 
	 * @param source
	 *            manual/delicious
	 * @return list with bookmarks
	 */
	List<BookmarkEntity> findBookmarksBySource(Source source);

	/**
	 * Increments the hits by 1
	 * 
	 * @param bookmark
	 *            bookmark
	 */
	void incrementHits(BookmarkEntity bookmark);

	/**
	 * Rates a bookmark
	 * 
	 * @param rating
	 *            rating value 1 to 5
	 * @param bookmark
	 *            bookmark
	 */
	void rateBookmark(Integer rating, BookmarkEntity bookmark);

	/**
	 * Marks a the given bookmark as broken
	 * 
	 * @param bookmark
	 *            bookmark
	 */
	void markBrokenBookmark(BookmarkEntity bookmark);

	/**
	 * Marks a the given bookmark as valid
	 * 
	 * @param bookmark
	 *            bookmark
	 */
	void markValidBookmark(BookmarkEntity bookmark);

	/**
	 * Returns the rights from the last editited bookmark
	 */
	List<RightEntity> findLastSelectedRights();
}
