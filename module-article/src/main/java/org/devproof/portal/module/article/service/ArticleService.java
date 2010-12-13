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

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleHistorized;
import org.devproof.portal.module.article.entity.ArticlePage;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public interface ArticleService extends CrudService<Article, Integer> {
    /**
     * Returns a new instance of Article
     *
     * @return new instance of {@link org.devproof.portal.module.article.entity.Article}
     */
    Article newArticleEntity();

    /**
     * Returns a new instance of ArticlePage
     *
     * @param article Article
     * @param page    page number
     * @return new instance of {@link org.devproof.portal.module.article.entity.Article}
     */
    ArticlePage newArticlePageEntity(Article article, Integer page);

    /**
     * Returns a list with all articles allowed for the role
     *
     * @param role        role to filter
     * @param firstResult first result number
     * @param maxResult   maximum result number
     * @return list with article entities
     */
    List<Article> findAllArticlesForRoleOrderedByDateDesc(Role role, Integer firstResult, Integer maxResult);

    /**
     * Returns true if a content id exists
     *
     * @param contentId content id
     * @return true if content id exists
     */
    boolean existsContentId(String contentId);

    /**
     * Number of pages of the article
     *
     * @param contentId content id
     * @return number of pages
     */
    long getPageCount(String contentId);

    /**
     * Returns the article page
     *
     * @param contentId content id
     * @param page      page number
     * @return {@link org.devproof.portal.module.article.entity.ArticlePage}
     */
    ArticlePage findArticlePageByContentIdAndPage(String contentId, Integer page);

    /**
     * Returns the Article by content id
     *
     * @param contentId content id
     * @return {@link org.devproof.portal.module.article.entity.Article}
     */
	Article findByContentId(String contentId);

    /**
     * Restores an article from history
     *
     * @param historized article
     */
    void restoreFromHistory(ArticleHistorized historized);
}
