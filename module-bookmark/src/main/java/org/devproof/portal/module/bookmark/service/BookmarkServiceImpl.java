/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.bookmark.service;

import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.entity.Bookmark.Source;
import org.devproof.portal.module.bookmark.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("bookmarkService")
public class BookmarkServiceImpl implements BookmarkService {
    private BookmarkRepository bookmarkRepository;
    private BookmarkTagService bookmarkTagService;

    @Override
    @Transactional(readOnly = true)
    public List<Bookmark> findAllBookmarksForRoleOrderedByDateDesc(Role role, Integer firstResult, Integer maxResult) {
        return bookmarkRepository.findAllBookmarksForRoleOrderedByDateDesc(role, firstResult, maxResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bookmark> findBookmarksBySource(Source source) {
        return bookmarkRepository.findBookmarksBySource(source);
    }

    @Override
    @Transactional
    public void incrementHits(Bookmark bookmark) {
        bookmarkRepository.incrementHits(bookmark);
    }

    @Override
    @Transactional
    public void markBrokenBookmark(Bookmark bookmark) {
        bookmarkRepository.markBrokenBookmark(bookmark);
    }

    @Override
    @Transactional
    public void markValidBookmark(Bookmark bookmark) {
        bookmarkRepository.markValidBookmark(bookmark);
    }

    @Override
    @Transactional(readOnly = true)
    public Bookmark newBookmarkEntity() {
        Bookmark bookmark = new Bookmark();
        bookmark.setAllRights(bookmarkRepository.findLastSelectedRights());
        return bookmark;
    }

    @Override
    @Transactional
    public void rateBookmark(Integer rating, Bookmark bookmark) {
        bookmarkRepository.rateBookmark(rating, bookmark);
        bookmarkRepository.refresh(bookmark);
    }

    @Override
    @Transactional
    public void delete(Bookmark entity) {
        bookmarkRepository.delete(entity);
        bookmarkTagService.deleteUnusedTags();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bookmark> findAll() {
        return bookmarkRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Bookmark findById(Integer id) {
        return bookmarkRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(Bookmark entity) {
        bookmarkRepository.save(entity);
        bookmarkTagService.deleteUnusedTags();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Right> findLastSelectedRights() {
        return bookmarkRepository.findLastSelectedRights();
    }

    @Autowired
    public void setBookmarkRepository(BookmarkRepository bookmarkRepository) {
        this.bookmarkRepository = bookmarkRepository;
    }

    @Autowired
    public void setBookmarkTagService(BookmarkTagService bookmarkTagService) {
        this.bookmarkTagService = bookmarkTagService;
    }
}
