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
import org.devproof.portal.core.module.historization.service.Historizer;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleHistorized;
import org.devproof.portal.module.article.repository.ArticleHistorizedRepository;
import org.devproof.portal.module.article.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Historizer for Article
 *
 * @author Carsten Hufe
 */
@Component
public class ArticleHistorizer implements Historizer<Article, ArticleHistorized> {
    private ArticleTagService articleTagService;
    private RightService rightService;
    private ArticleHistorizedRepository articleHistorizedRepository;
    private ArticleRepository articleRepository;

    @Override
    public void historize(Article article, Action action) {
        historize(article, action, null);
    }

    private void historize(Article article, Action action, Integer restoredVersion) {
        ArticleHistorized historized = new ArticleHistorized();
        historized.copyFrom(article);
        historized.setTags(articleTagService.convertTagsToWhitespaceSeparated(article.getTags()));
        historized.setRights(rightService.convertRightsToWhitespaceSeparated(article.getAllRights()));
        historized.setArticle(article);
        historized.setAction(action);
        historized.setActionAt(new Date());
        historized.setVersionNumber(retrieveNextVersionNumber(article));
        historized.setRestoredFromVersion(restoredVersion);
        articleHistorizedRepository.save(historized);
    }

    private Integer retrieveNextVersionNumber(Article article) {
        Integer nextNumber = articleHistorizedRepository.findLastVersionNumber(article);
        if(nextNumber == null) {
            nextNumber = 0;
        }
        return nextNumber + 1;
    }

    @Override
    public Article restore(ArticleHistorized historized) {
        Article article = historized.getArticle();
        article.copyFrom(historized);
        article.setAllRights(rightService.findWhitespaceSeparatedRights(historized.getRights()));
        article.setTags(articleTagService.findWhitespaceSeparatedTagsAndCreateIfNotExists(historized.getTags()));
        article.setUpdateModificationData(false);
        historize(article, Action.RESTORED, historized.getVersionNumber());
        articleRepository.save(article);
        return article;
    }

    @Override
    public void deleteHistory(Article article) {
        articleHistorizedRepository.deleteHistoryForArticle(article);
    }

    @Autowired
    public void setRightService(RightService rightService) {
        this.rightService = rightService;
    }

    @Autowired
    public void setArticleTagService(ArticleTagService articleTagService) {
        this.articleTagService = articleTagService;
    }

    @Autowired
    public void setArticleHistorizedRepository(ArticleHistorizedRepository articleHistorizedRepository) {
        this.articleHistorizedRepository = articleHistorizedRepository;
    }

    @Autowired
    public void setArticleRepository(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
}
