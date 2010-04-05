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
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.dao.BookmarkDao;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
public class BookmarkServiceImplTest {
    private BookmarkServiceImpl impl;
    private BookmarkDao mock;
    private TagService<BookmarkTagEntity> mockTag;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mock = createStrictMock(BookmarkDao.class);
        mockTag = (TagService<BookmarkTagEntity>) createStrictMock(TagService.class);
        impl = new BookmarkServiceImpl();
        impl.setBookmarkDao(mock);
        impl.setBookmarkTagService(mockTag);
    }

    @Test
    public void testSave() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        expect(mock.save(e)).andReturn(e);
        mockTag.deleteUnusedTags();
        replay(mock);
        replay(mockTag);
        impl.save(e);
        verify(mock);
        verify(mockTag);
    }

    @Test
    public void testDelete() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        mock.delete(e);
        mockTag.deleteUnusedTags();
        replay(mock);
        replay(mockTag);
        impl.delete(e);
        verify(mock);
        verify(mockTag);
    }

    @Test
    public void testFindAll() {
        List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
        list.add(createBookmarkEntity());
        list.add(createBookmarkEntity());
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAll());
        verify(mock);
    }

    @Test
    public void testFindById() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        expect(mock.findById(1)).andReturn(e);
        replay(mock);
        assertEquals(impl.findById(1), e);
        verify(mock);
    }

    @Test
    public void testNewBookmarkEntity() {
        assertNotNull(impl.newBookmarkEntity());
    }

    @Test
    public void testFindAllBookmarksForRoleOrderedByDateDesc() {
        List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
        list.add(createBookmarkEntity());
        list.add(createBookmarkEntity());
        RoleEntity role = new RoleEntity();
        role.setId(1);
        expect(mock.findAllBookmarksForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
        replay(mock);
        impl.findAllBookmarksForRoleOrderedByDateDesc(role, 0, 2);
        verify(mock);
    }

    @Test
    public void testFindBookmarksBySource() {
        List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
        list.add(createBookmarkEntity());
        list.add(createBookmarkEntity());
        expect(mock.findBookmarksBySource(Source.DELICIOUS)).andReturn(list);
        replay(mock);
        impl.findBookmarksBySource(Source.DELICIOUS);
        verify(mock);
    }

    @Test
    public void testIncrementHits() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        mock.incrementHits(e);
        replay(mock);
        impl.incrementHits(e);
        verify(mock);
    }

    @Test
    public void testMarkBrokenBookmark() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        mock.markBrokenBookmark(e);
        replay(mock);
        impl.markBrokenBookmark(e);
        verify(mock);
    }

    @Test
    public void testMarkValidBookmark() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        mock.markValidBookmark(e);
        replay(mock);
        impl.markValidBookmark(e);
        verify(mock);
    }

    @Test
    public void testRateBookmark() {
        BookmarkEntity e = createBookmarkEntity();
        e.setId(1);
        mock.rateBookmark(5, e);
        mock.refresh(e);
        replay(mock);
        impl.rateBookmark(5, e);
        verify(mock);
    }

    @Test
    public void testFindLastSelectedRightsk() {
        List<RightEntity> list = new ArrayList<RightEntity>();
        expect(mock.findLastSelectedRights()).andReturn(list);
        replay(mock);
        assertTrue(impl.findLastSelectedRights() == list);
        verify(mock);
    }

    private BookmarkEntity createBookmarkEntity() {
        return new BookmarkEntity();
    }
}
