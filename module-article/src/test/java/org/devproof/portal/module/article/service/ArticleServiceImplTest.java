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
		mock = EasyMock.createStrictMock(ArticleDao.class);
		mockPage = EasyMock.createStrictMock(ArticlePageDao.class);
		@SuppressWarnings("unchecked")
		TagService<ArticleTagEntity> tagService = EasyMock.createStrictMock(TagService.class);
		mockTag = tagService;
		impl = new ArticleServiceImpl();
		impl.setArticleDao(mock);
		impl.setArticlePageDao(mockPage);
		impl.setArticleTagService(mockTag);
	}

	public void testSave() {
		ArticleEntity e = impl.newArticleEntity();
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
		ArticleEntity e = impl.newArticleEntity();
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
		List<ArticleEntity> list = new ArrayList<ArticleEntity>();
		list.add(impl.newArticleEntity());
		list.add(impl.newArticleEntity());
		EasyMock.expect(mock.findAll()).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(list, impl.findAll());
		EasyMock.verify(mock);
	}

	public void testFindById() {
		ArticleEntity e = impl.newArticleEntity();
		e.setId(1);
		EasyMock.expect(mock.findById(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findById(1), e);
		EasyMock.verify(mock);
	}

	public void testFindByIdAndPrefetch() {
		ArticleEntity e = impl.newArticleEntity();
		e.setId(1);
		EasyMock.expect(mock.findByIdAndPrefetch(1)).andReturn(e);
		EasyMock.replay(mock);
		assertEquals(impl.findByIdAndPrefetch(1), e);
		EasyMock.verify(mock);
	}

	public void testNewArticleEntity() {
		assertNotNull(impl.newArticleEntity());
	}

	public void testNewArticlePageEntity() {
		ArticleEntity a = impl.newArticleEntity();
		a.setId(1);
		ArticlePageEntity ap = impl.newArticlePageEntity(a, 1);
		assertNotNull(ap);
		assertEquals(a, ap.getArticle());
	}

	public void testGetPageCount() {
		EasyMock.expect(mockPage.getPageCount("contentId")).andReturn(4l);
		EasyMock.replay(mockPage);
		assertEquals(impl.getPageCount("contentId"), 4l);
		EasyMock.verify(mockPage);
	}

	public void testExistsContentId() {
		EasyMock.expect(mock.existsContentId("contentId")).andReturn(1l);
		EasyMock.replay(mock);
		assertTrue(impl.existsContentId("contentId"));
		EasyMock.verify(mock);
	}

	public void testFindAllArticlesForRoleOrderedByDateDesc() {
		RoleEntity role = new RoleEntity();
		List<ArticleEntity> list = new ArrayList<ArticleEntity>();
		list.add(impl.newArticleEntity());
		list.add(impl.newArticleEntity());
		EasyMock.expect(mock.findAllArticlesForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
		EasyMock.replay(mock);
		assertEquals(impl.findAllArticlesForRoleOrderedByDateDesc(role, 0, 2), list);
		EasyMock.verify(mock);
	}
}
