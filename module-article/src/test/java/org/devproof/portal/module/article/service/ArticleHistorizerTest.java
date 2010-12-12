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

import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleHistorized;
import org.devproof.portal.module.article.entity.ArticleTag;
import org.devproof.portal.module.article.repository.ArticleHistorizedRepository;
import org.devproof.portal.module.article.repository.ArticleRepository;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;

/**
 * @author Carsten Hufe
 */
public class ArticleHistorizerTest {
    private ArticleHistorizer impl;
    private ArticleRepository mockRepo;
    private ArticleTagService mockTagService;
    private RightService mockRightService;
    private ArticleHistorizedRepository mockHistorizedRepo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mockRepo = createStrictMock(ArticleRepository.class);
        mockTagService = createStrictMock(ArticleTagService.class);
        mockRightService = createStrictMock(RightService.class);
        mockHistorizedRepo = createStrictMock(ArticleHistorizedRepository.class);
        impl = new ArticleHistorizer();
        impl.setArticleRepository(mockRepo);
        impl.setRightService(mockRightService);
        impl.setArticleHistorizedRepository(mockHistorizedRepo);
        impl.setArticleTagService(mockTagService);
    }


    @Test
    public void testHistorize() throws Exception {
        expect(mockTagService.convertTagsToWhitespaceSeparated(EasyMock.<List<ArticleTag>>anyObject())).andReturn("tag1 tag2");
        expect(mockRightService.convertRightsToWhitespaceSeparated(EasyMock.<List<Right>>anyObject())).andReturn("right1 right2");
        expect(mockHistorizedRepo.findLastVersionNumber(anyObject(Article.class))).andReturn(1);
        expect(mockHistorizedRepo.save(EasyMock.<ArticleHistorized>anyObject())).andReturn(null);
        replay(mockTagService, mockRightService, mockHistorizedRepo);
        Article article = new Article();
        article.setId(1);
        article.setTitle("headline");
        article.setFullArticle("content");
        impl.historize(article, Action.CREATED);
        verify(mockTagService, mockRightService, mockHistorizedRepo);
    }

    @Test
    public void testRestore() throws Exception {
        expect(mockRightService.findWhitespaceSeparatedRights(anyObject(String.class))).andReturn(new ArrayList<Right>());
        expect(mockTagService.findWhitespaceSeparatedTagsAndCreateIfNotExists(anyObject(String.class))).andReturn(new ArrayList<ArticleTag>());
        // historize again
        expect(mockTagService.convertTagsToWhitespaceSeparated(EasyMock.<List<ArticleTag>>anyObject())).andReturn("tag1 tag2");
        expect(mockRightService.convertRightsToWhitespaceSeparated(EasyMock.<List<Right>>anyObject())).andReturn("right1 right2");
        expect(mockHistorizedRepo.findLastVersionNumber(anyObject(Article.class))).andReturn(1);
        expect(mockHistorizedRepo.save(EasyMock.<ArticleHistorized>anyObject())).andReturn(null);
        // restore
        expect(mockRepo.save(anyObject(Article.class))).andReturn(new Article());
        replay(mockTagService, mockRightService, mockHistorizedRepo, mockRepo);
        ArticleHistorized historized = new ArticleHistorized();
        historized.setArticle(new Article());
        Article restoredArticle = impl.restore(historized);
        assertFalse(restoredArticle.isUpdateModificationData());
        verify(mockTagService, mockRightService, mockHistorizedRepo, mockRepo);
    }

    @Test
    public void testDeleteHistory() throws Exception {
        mockHistorizedRepo.deleteHistoryForArticle(anyObject(Article.class));
        replay(mockHistorizedRepo);
        impl.deleteHistory(new Article());
        verify(mockHistorizedRepo);
    }
}
