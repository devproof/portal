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

import junit.framework.TestCase;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.blog.dao.BlogDao;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.entity.BlogTagEntity;

import static org.easymock.EasyMock.*;

/**
 * @author Carsten Hufe
 */
public class BlogServiceImplTest extends TestCase {
	private BlogServiceImpl impl;
	private BlogDao mock;
	private TagService<BlogTagEntity> mockTag;

	@Override
	public void setUp() throws Exception {
		mock = createStrictMock(BlogDao.class);
		@SuppressWarnings("unchecked")
		TagService<BlogTagEntity> tagService = createStrictMock(TagService.class);
		mockTag = tagService;
		impl = new BlogServiceImpl();
		impl.setBlogDao(mock);
		impl.setBlogTagService(mockTag);
	}

	public void testSave() {
		BlogEntity e = createBlogEntity();
		expect(mock.save(e)).andReturn(e);
		mockTag.deleteUnusedTags();
		replay(mock);
		replay(mockTag);
		impl.save(e);
		verify(mock);
		verify(mockTag);
	}

	public void testDelete() {
		BlogEntity e = createBlogEntity();
		mock.delete(e);
		mockTag.deleteUnusedTags();
		replay(mock);
		replay(mockTag);
		impl.delete(e);
		verify(mock);
		verify(mockTag);
	}

	public void testFindById() {
		BlogEntity e = createBlogEntity();
		expect(mock.findById(1)).andReturn(e);
		replay(mock);
		assertEquals(impl.findById(1), e);
		verify(mock);
	}

	public void testNewBlogEntity() {
		assertNotNull(impl.newBlogEntity());
	}

	private BlogEntity createBlogEntity() {
		BlogEntity blog = new BlogEntity();
		blog.setId(1);
		return blog;
	}
}
