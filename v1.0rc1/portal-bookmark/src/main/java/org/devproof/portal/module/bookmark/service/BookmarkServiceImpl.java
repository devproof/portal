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
package org.devproof.portal.module.bookmark.service;

import java.util.List;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.dao.BookmarkDao;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;

/**
 * @author Carsten Hufe
 */
public class BookmarkServiceImpl implements BookmarkService {
	private BookmarkDao bookmarkDao;
	private TagService<BookmarkTagEntity> bookmarkTagService;

	@Override
	public List<BookmarkEntity> findAllBookmarksForRoleOrderedByDateDesc(final RoleEntity role, final Integer firstResult, final Integer maxResult) {
		return this.bookmarkDao.findAllBookmarksForRoleOrderedByDateDesc(role, firstResult, maxResult);
	}

	@Override
	public List<BookmarkEntity> findBookmarksBySource(final Source source) {
		return this.bookmarkDao.findBookmarksBySource(source);
	}

	@Override
	public void incrementHits(final BookmarkEntity bookmark) {
		this.bookmarkDao.incrementHits(bookmark);
	}

	@Override
	public void markBrokenBookmark(final BookmarkEntity bookmark) {
		this.bookmarkDao.markBrokenBookmark(bookmark);
	}

	@Override
	public void markValidBookmark(final BookmarkEntity bookmark) {
		this.bookmarkDao.markValidBookmark(bookmark);
	}

	@Override
	public BookmarkEntity newBookmarkEntity() {
		return new BookmarkEntity();
	}

	@Override
	public void rateBookmark(final Integer rating, final BookmarkEntity bookmark) {
		this.bookmarkDao.rateBookmark(rating, bookmark);
		this.bookmarkDao.refresh(bookmark);
	}

	@Override
	public void delete(final BookmarkEntity entity) {
		this.bookmarkDao.delete(entity);
		this.bookmarkTagService.deleteUnusedTags();
	}

	@Override
	public List<BookmarkEntity> findAll() {
		return this.bookmarkDao.findAll();
	}

	@Override
	public BookmarkEntity findById(final Integer id) {
		return this.bookmarkDao.findById(id);
	}

	@Override
	public void save(final BookmarkEntity entity) {
		this.bookmarkDao.save(entity);
		this.bookmarkTagService.deleteUnusedTags();
	}

	public void setBookmarkDao(final BookmarkDao bookmarkDao) {
		this.bookmarkDao = bookmarkDao;
	}

	public void setBookmarkTagService(final TagService<BookmarkTagEntity> bookmarkTagService) {
		this.bookmarkTagService = bookmarkTagService;
	}
}
