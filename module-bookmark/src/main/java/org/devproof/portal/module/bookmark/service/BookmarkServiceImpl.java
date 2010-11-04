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
package org.devproof.portal.module.bookmark.service;

import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.bookmark.repository.BookmarkRepository;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("bookmarkService")
public class BookmarkServiceImpl implements BookmarkService {
    private BookmarkRepository bookmarkRepository;
    private BookmarkTagService bookmarkTagService;

    @Override
    public List<BookmarkEntity> findAllBookmarksForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult, Integer maxResult) {
        return bookmarkRepository.findAllBookmarksForRoleOrderedByDateDesc(role, firstResult, maxResult);
    }

    @Override
    public List<BookmarkEntity> findBookmarksBySource(Source source) {
        return bookmarkRepository.findBookmarksBySource(source);
    }

    @Override
    public void incrementHits(BookmarkEntity bookmark) {
        bookmarkRepository.incrementHits(bookmark);
    }

    @Override
    public void markBrokenBookmark(BookmarkEntity bookmark) {
        bookmarkRepository.markBrokenBookmark(bookmark);
    }

    @Override
    public void markValidBookmark(BookmarkEntity bookmark) {
        bookmarkRepository.markValidBookmark(bookmark);
    }

    @Override
    public BookmarkEntity newBookmarkEntity() {
        BookmarkEntity bookmark = new BookmarkEntity();
        bookmark.setAllRights(bookmarkRepository.findLastSelectedRights());
        return bookmark;
    }

    @Override
    public void rateBookmark(Integer rating, BookmarkEntity bookmark) {
        bookmarkRepository.rateBookmark(rating, bookmark);
        bookmarkRepository.refresh(bookmark);
    }

    @Override
    public void delete(BookmarkEntity entity) {
        bookmarkRepository.delete(entity);
        bookmarkTagService.deleteUnusedTags();
    }

    @Override
    public List<BookmarkEntity> findAll() {
        return bookmarkRepository.findAll();
    }

    @Override
    public BookmarkEntity findById(Integer id) {
        return bookmarkRepository.findById(id);
    }

    @Override
    public void save(BookmarkEntity entity) {
        bookmarkRepository.save(entity);
        bookmarkTagService.deleteUnusedTags();
    }

    @Override
    public List<RightEntity> findLastSelectedRights() {
        return bookmarkRepository.findLastSelectedRights();
    }

    @Autowired
    public void setBookmarkDao(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Autowired
    public void setBookmarkTagService(BookmarkTagService bookmarkTagService) {
        this.bookmarkTagService = bookmarkTagService;
    }
}
