/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.article.service;

import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.core.module.mount.service.MountService;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleHistorized;
import org.devproof.portal.module.article.entity.ArticlePage;
import org.devproof.portal.module.article.mount.ArticleMountHandler;
import org.devproof.portal.module.article.repository.ArticlePageRepository;
import org.devproof.portal.module.article.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("articleService")
public class ArticleServiceImpl implements ArticleService {
    private ArticleRepository articleRepository;
    private ArticlePageRepository articlePageRepository;
    private ArticleTagService articleTagService;
    private ArticleHistorizer articleHistorizer;
    private MountService mountService;

    @Override
    @Transactional(readOnly = true)
    public List<Article> findAllArticlesForRoleOrderedByDateDesc(Role role, Integer firstResult, Integer maxResult) {
        return articleRepository.findAllArticlesForRoleOrderedByDateDesc(role, firstResult, maxResult);
    }

    @Override
    @Transactional(readOnly = true)
    public Article newArticleEntity() {
        Article article = new Article();
        article.setAllRights(articleRepository.findLastSelectedRights());
        return article;
    }

    @Override
    @Transactional(readOnly = true)
    public ArticlePage newArticlePageEntity(Article article, Integer page) {
        return article.newArticlePageEntity(page);
    }

    @Override
    @Transactional
    public void delete(Article entity) {
        articleHistorizer.deleteHistory(entity);
        articleRepository.delete(entity);
        articleTagService.deleteUnusedTags();
        mountService.delete(entity.getId().toString(), ArticleConstants.HANDLER_KEY);
    }

    @Override
    @Transactional(readOnly = true)
    public Article findById(Integer id) {
        return articleRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(Article entity) {
        Action action = entity.isTransient() ? Action.CREATED : Action.MODIFIED;
        articleRepository.save(entity);
        articleTagService.deleteUnusedTags();
        articleHistorizer.historize(entity, action);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPageCount(Integer articleId) {
        return articlePageRepository.getPageCount(articleId);
    }

    @Override
    @Transactional(readOnly = true)
    public ArticlePage findArticlePageByArticleIdAndPage(Integer articleId, Integer page) {
        return articlePageRepository.findByArticleIdAndPage(articleId, page);
    }

    @Override
    @Transactional
    public void restoreFromHistory(ArticleHistorized historized) {
        articleHistorizer.restore(historized);
    }

    @Autowired
    public void setArticleRepository(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Autowired
    public void setArticlePageRepository(ArticlePageRepository articlePageRepository) {
        this.articlePageRepository = articlePageRepository;
    }

    @Autowired
    public void setArticleTagService(ArticleTagService articleTagService) {
        this.articleTagService = articleTagService;
    }

    @Autowired
    public void setArticleHistorizer(ArticleHistorizer articleHistorizer) {
        this.articleHistorizer = articleHistorizer;
    }

    @Autowired
    public void setMountService(MountService mountService) {
        this.mountService = mountService;
    }
}
