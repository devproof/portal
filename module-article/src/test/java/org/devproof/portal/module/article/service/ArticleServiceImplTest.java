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
package org.devproof.portal.module.article.service;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticlePage;
import org.devproof.portal.module.article.repository.ArticlePageRepository;
import org.devproof.portal.module.article.repository.ArticleRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Carsten Hufe
 */
public class ArticleServiceImplTest {
    private ArticleServiceImpl impl;
    private ArticleRepository mock;
    private ArticlePageRepository mockPage;
    private ArticleTagService mockTag;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mock = createStrictMock(ArticleRepository.class);
        mockPage = createStrictMock(ArticlePageRepository.class);
        mockTag = createStrictMock(ArticleTagService.class);
        impl = new ArticleServiceImpl();
        impl.setArticleRepository(mock);
        impl.setArticlePageRepository(mockPage);
        impl.setArticleTagService(mockTag);
    }

    @Test
    public void testSave() {
        Article e = createArticleEntity();
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
        Article e = createArticleEntity();
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
    public void testFindById() {
        Article e = createArticleEntity();
        e.setId(1);
        expect(mock.findById(1)).andReturn(e);
        replay(mock);
        assertEquals(impl.findById(1), e);
        verify(mock);
    }

    @Test
    public void testNewArticleEntity() {
        assertNotNull(impl.newArticleEntity());
    }

    @Test
    public void testNewArticlePageEntity() {
        Article a = createArticleEntity();
        a.setId(1);
        ArticlePage ap = impl.newArticlePageEntity(a, 1);
        assertNotNull(ap);
        assertEquals(a, ap.getArticle());
    }

    @Test
    public void testGetPageCount() {
        expect(mockPage.getPageCount("contentId")).andReturn(4l);
        replay(mockPage);
        assertEquals(impl.getPageCount("contentId"), 4l);
        verify(mockPage);
    }

    @Test
    public void testExistsContentId() {
        expect(mock.existsContentId("contentId")).andReturn(1l);
        replay(mock);
        assertTrue(impl.existsContentId("contentId"));
        verify(mock);
    }

    @Test
    public void testFindAllArticlesForRoleOrderedByDateDesc() {
        RoleEntity role = new RoleEntity();
        List<Article> list = new ArrayList<Article>();
        list.add(createArticleEntity());
        list.add(createArticleEntity());
        expect(mock.findAllArticlesForRoleOrderedByDateDesc(role, 0, 2)).andReturn(list);
        replay(mock);
        assertEquals(impl.findAllArticlesForRoleOrderedByDateDesc(role, 0, 2), list);
        verify(mock);
    }

    private Article createArticleEntity() {
        return new Article();
    }
}
