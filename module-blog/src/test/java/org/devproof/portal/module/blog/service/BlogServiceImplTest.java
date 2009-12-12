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
package org.devproof.portal.module.blog.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.blog.dao.BlogDao;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.entity.BlogTagEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class BlogServiceImplTest extends TestCase {
	private BlogServiceImpl impl;
	private BlogDao mock;
	private TagService<BlogTagEntity> mockTag;

	@Override
	public void setUp() throws Exception {
		mock = EasyMock.createStrictMock(BlogDao.class);
		@SuppressWarnings("unchecked")
		TagService<BlogTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		mockTag = tagService;
		impl = new BlogServiceImpl();
		impl.setBlogDao(mock);
		impl.setBlogTagService(mockTag);
	}

	public void testSave() {
		BlogEntity e = impl.newBlogEntity();
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
		BlogEntity e = impl.newBlogEntity();
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
		List<BlogEntity> list = new ArrayList<BlogEntity>();
		list.add(impl.newBlogEntity());
		list.add(impl.newBlogEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		BlogEntity e = impl.newBlogEntity();
		e.setId(1);
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testNewBlogEntity() {
		assertNotNull(impl.newBlogEntity());
	}
}
