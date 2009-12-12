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
		this.mock = EasyMock.createStrictMock(BlogDao.class);
		@SuppressWarnings("unchecked")
		TagService<BlogTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		this.mockTag = tagService;
		this.impl = new BlogServiceImpl();
		this.impl.setBlogDao(this.mock);
		this.impl.setBlogTagService(this.mockTag);
	}

	public void testSave() {
		BlogEntity e = this.impl.newBlogEntity();
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
		BlogEntity e = this.impl.newBlogEntity();
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
		List<BlogEntity> list = new ArrayList<BlogEntity>();
		list.add(this.impl.newBlogEntity());
		list.add(this.impl.newBlogEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		BlogEntity e = this.impl.newBlogEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewBlogEntity() {
		assertNotNull(this.impl.newBlogEntity());
	}
}
