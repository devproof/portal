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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.dao.BookmarkDao;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class BookmarkServiceImplTest extends TestCase {
	private BookmarkServiceImpl impl;
	private BookmarkDao mock;
	private TagService<BookmarkTagEntity> mockTag;

	@Override
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(BookmarkDao.class);
		@SuppressWarnings("unchecked")
		TagService<BookmarkTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		mockTag = tagService;
		impl = new BookmarkServiceImpl();
		impl.setBookmarkDao(mock);
		impl.setBookmarkTagService(mockTag);
	}

	public void testSave() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		mock.save(e);
		mockTag.deleteUnusedTags();
		EasyMock.replay(mock);
		EasyMock.replay(mockTag);
		impl.save(e);
		EasyMock.verify(mock);
		EasyMock.verify(mockTag);
	}

	public void testDelete() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		mock.delete(e);
		mockTag.deleteUnusedTags();
		EasyMock.replay(mock);
		EasyMock.replay(mockTag);
		impl.delete(e);
		EasyMock.verify(mock);
		EasyMock.verify(mockTag);
	}

	public void testFindAll() {
		List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
		list.add(impl.newBookmarkEntity());
		list.add(impl.newBookmarkEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewBookmarkEntity() {
		assertNotNull(impl.newBookmarkEntity());
	}

	public void testFindAllBookmarksForRoleOrderedByDateDesc() {
		List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
		list.add(impl.newBookmarkEntity());
		list.add(impl.newBookmarkEntity());
		RoleEntity role = new RoleEntity();
		role.setId(1);
		EasyMock.expect(mock.findAllBookmarksForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
		EasyMock.replay(mock);
		impl.findAllBookmarksForRoleOrderedByDateDesc(role, 0, 2);
		EasyMock.verify(mock);
	}

	public void testFindBookmarksBySource() {
		List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
		list.add(impl.newBookmarkEntity());
		list.add(impl.newBookmarkEntity());
		EasyMock.expect(mock.findBookmarksBySource(Source.DELICIOUS)).andReturn(list);
		EasyMock.replay(mock);
		impl.findBookmarksBySource(Source.DELICIOUS);
		EasyMock.verify(mock);
	}

	public void testIncrementHits() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		mock.incrementHits(e);
		EasyMock.replay(mock);
		impl.incrementHits(e);
		EasyMock.verify(mock);
	}

	public void testMarkBrokenBookmark() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		mock.markBrokenBookmark(e);
		EasyMock.replay(mock);
		impl.markBrokenBookmark(e);
		EasyMock.verify(mock);
	}

	public void testMarkValidBookmark() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		mock.markValidBookmark(e);
		EasyMock.replay(mock);
		impl.markValidBookmark(e);
		EasyMock.verify(mock);
	}

	public void testRateBookmark() {
		BookmarkEntity e = impl.newBookmarkEntity();
		e.setId(1);
		mock.rateBookmark(5, e);
		mock.refresh(e);
		EasyMock.replay(mock);
		impl.rateBookmark(5, e);
		EasyMock.verify(mock);
	}
}
