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

import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.dao.BookmarkDao;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class BookmarkServiceImpl implements BookmarkService {
	private BookmarkDao bookmarkDao;
	private TagService<BookmarkTagEntity> bookmarkTagService;

	@Override
	public List<BookmarkEntity> findAllBookmarksForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult,
			Integer maxResult) {
		return bookmarkDao.findAllBookmarksForRoleOrderedByDateDesc(role, firstResult, maxResult);
	}

	@Override
	public List<BookmarkEntity> findBookmarksBySource(Source source) {
		return bookmarkDao.findBookmarksBySource(source);
	}

	@Override
	public void incrementHits(BookmarkEntity bookmark) {
		bookmarkDao.incrementHits(bookmark);
	}

	@Override
	public void markBrokenBookmark(BookmarkEntity bookmark) {
		bookmarkDao.markBrokenBookmark(bookmark);
	}

	@Override
	public void markValidBookmark(BookmarkEntity bookmark) {
		bookmarkDao.markValidBookmark(bookmark);
	}

	@Override
	public BookmarkEntity newBookmarkEntity() {
		BookmarkEntity bookmark = new BookmarkEntity();
		bookmark.setAllRights(bookmarkDao.findLastSelectedRights());
		return bookmark;
	}

	@Override
	public void rateBookmark(Integer rating, BookmarkEntity bookmark) {
		bookmarkDao.rateBookmark(rating, bookmark);
		bookmarkDao.refresh(bookmark);
	}

	@Override
	public void delete(BookmarkEntity entity) {
		bookmarkDao.delete(entity);
		bookmarkTagService.deleteUnusedTags();
	}

	@Override
	public List<BookmarkEntity> findAll() {
		return bookmarkDao.findAll();
	}

	@Override
	public BookmarkEntity findById(Integer id) {
		return bookmarkDao.findById(id);
	}

	@Override
	public void save(BookmarkEntity entity) {
		bookmarkDao.save(entity);
		bookmarkTagService.deleteUnusedTags();
	}

	@Override
	public List<RightEntity> findLastSelectedRights() {
		return bookmarkDao.findLastSelectedRights();
	}

	@Required
	public void setBookmarkDao(BookmarkDao bookmarkDao) {
		this.bookmarkDao = bookmarkDao;
	}

	@Required
	public void setBookmarkTagService(TagService<BookmarkTagEntity> bookmarkTagService) {
		this.bookmarkTagService = bookmarkTagService;
	}
}
