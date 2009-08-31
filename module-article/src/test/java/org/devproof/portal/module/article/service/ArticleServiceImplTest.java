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
package org.devproof.portal.module.article.service;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.dao.ArticleDao;
import org.devproof.portal.module.article.dao.ArticlePageDao;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.easymock.EasyMock;

/**
 * @author Carsten Hufe
 */
public class ArticleServiceImplTest extends TestCase {
	private ArticleServiceImpl impl;
	private ArticleDao mock;
	private ArticlePageDao mockPage;
	private TagService<ArticleTagEntity> mockTag;

	@Override
	public void setUp() throws Exception {
		this.mock = EasyMock.createStrictMock(ArticleDao.class);
		this.mockPage = EasyMock.createStrictMock(ArticlePageDao.class);
		@SuppressWarnings("unchecked")
		TagService<ArticleTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		this.mockTag = tagService;
		this.impl = new ArticleServiceImpl();
		this.impl.setArticleDao(this.mock);
		this.impl.setArticlePageDao(this.mockPage);
		this.impl.setArticleTagService(this.mockTag);
	}

	public void testSave() {
		ArticleEntity e = this.impl.newArticleEntity();
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
		ArticleEntity e = this.impl.newArticleEntity();
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
		List<ArticleEntity> list = new ArrayList<ArticleEntity>();
		list.add(this.impl.newArticleEntity());
		list.add(this.impl.newArticleEntity());
		EasyMock.expect(this.mock.findAll()).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(list, this.impl.findAll());
		EasyMock.verify(this.mock);
	}

	public void testFindById() {
		ArticleEntity e = this.impl.newArticleEntity();
		e.setId(1);
		EasyMock.expect(this.mock.findById(1)).andReturn(e);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findById(1), e);
		EasyMock.verify(this.mock);
	}

	public void testNewArticleEntity() {
		assertNotNull(this.impl.newArticleEntity());
	}

	public void testNewArticlePageEntity() {
		ArticleEntity a = this.impl.newArticleEntity();
		a.setId(1);
		ArticlePageEntity ap = this.impl.newArticlePageEntity(a, 1);
		assertNotNull(ap);
		assertEquals(a, ap.getArticle());
	}

	public void testGetPageCount() {
		EasyMock.expect(this.mockPage.getPageCount("contentId")).andReturn(4l);
		EasyMock.replay(this.mockPage);
		assertEquals(this.impl.getPageCount("contentId"), 4l);
		EasyMock.verify(this.mockPage);
	}

	public void testExistsContentId() {
		EasyMock.expect(this.mock.existsContentId("contentId")).andReturn(1l);
		EasyMock.replay(this.mock);
		assertTrue(this.impl.existsContentId("contentId"));
		EasyMock.verify(this.mock);
	}

	public void testFindAllArticlesForRoleOrderedByDateDesc() {
		RoleEntity role = new RoleEntity();
		List<ArticleEntity> list = new ArrayList<ArticleEntity>();
		list.add(this.impl.newArticleEntity());
		list.add(this.impl.newArticleEntity());
		EasyMock.expect(this.mock.findAllArticlesForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
		EasyMock.replay(this.mock);
		assertEquals(this.impl.findAllArticlesForRoleOrderedByDateDesc(role, 0, 2), list);
		EasyMock.verify(this.mock);
	}
}
