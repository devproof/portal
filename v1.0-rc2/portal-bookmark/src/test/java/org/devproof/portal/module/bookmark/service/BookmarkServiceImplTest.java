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
		this.mock = EasyMock.createStrictMock(BookmarkDao.class);
		@SuppressWarnings("unchecked")
		TagService<BookmarkTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		this.mockTag = tagService;
		this.impl = new BookmarkServiceImpl();
		this.impl.setBookmarkDao(this.mock);
		this.impl.setBookmarkTagService(this.mockTag);
	}

	public void testSave() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		this.mock.save(e);
		this.mockTag.deleteUnusedTags();
		EasyMock.replay(this.mock);
		EasyMock.replay(this.mockTag);
		this.impl.save(e);
		EasyMock.verify(this.mock);
		EasyMock.verify(this.mockTag);
	}

	public void testDelete() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		this.mock.delete(e);
		this.mockTag.deleteUnusedTags();
		EasyMock.replay(this.mock);
		EasyMock.replay(this.mockTag);
		this.impl.delete(e);
		EasyMock.verify(this.mock);
		EasyMock.verify(this.mockTag);
	}

	public void testFindAll() {
		List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
		list.add(this.impl.newBookmarkEntity());
		list.add(this.impl.newBookmarkEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewBookmarkEntity() {
		assertNotNull(this.impl.newBookmarkEntity());
	}

	public void testFindAllBookmarksForRoleOrderedByDateDesc() {
		List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
		list.add(this.impl.newBookmarkEntity());
		list.add(this.impl.newBookmarkEntity());
		RoleEntity role = new RoleEntity();
		role.setId(1);
		EasyMock.expect(this.mock.findAllBookmarksForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
		EasyMock.replay(this.mock);
		this.impl.findAllBookmarksForRoleOrderedByDateDesc(role, 0, 2);
		EasyMock.verify(this.mock);
	}

	public void testFindBookmarksBySource() {
		List<BookmarkEntity> list = new ArrayList<BookmarkEntity>();
		list.add(this.impl.newBookmarkEntity());
		list.add(this.impl.newBookmarkEntity());
		EasyMock.expect(this.mock.findBookmarksBySource(Source.DELICIOUS)).andReturn(list);
		EasyMock.replay(this.mock);
		this.impl.findBookmarksBySource(Source.DELICIOUS);
		EasyMock.verify(this.mock);
	}

	public void testIncrementHits() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		this.mock.incrementHits(e);
		EasyMock.replay(this.mock);
		this.impl.incrementHits(e);
		EasyMock.verify(this.mock);
	}

	public void testMarkBrokenBookmark() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		this.mock.markBrokenBookmark(e);
		EasyMock.replay(this.mock);
		this.impl.markBrokenBookmark(e);
		EasyMock.verify(this.mock);
	}

	public void testMarkValidBookmark() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		this.mock.markValidBookmark(e);
		EasyMock.replay(this.mock);
		this.impl.markValidBookmark(e);
		EasyMock.verify(this.mock);
	}

	public void testRateBookmark() {
		BookmarkEntity e = this.impl.newBookmarkEntity();
		e.setId(1);
		this.mock.rateBookmark(5, e);
		this.mock.refresh(e);
		EasyMock.replay(this.mock);
		this.impl.rateBookmark(5, e);
		EasyMock.verify(this.mock);
	}
}
