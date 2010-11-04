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
package org.devproof.portal.module.article.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.Article;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@GenericRepository("articleRepository")
@CacheQuery(region = ArticleConstants.QUERY_CACHE_REGION)
public interface ArticleRepository extends CrudRepository<Article, Integer> {
    @CacheQuery(enabled = false)
    @Query("select a.allRights from Article a where a.modifiedAt = (select max(modifiedAt) from Article)")
    List<Right> findLastSelectedRights();

    @Query("select a from Article a where a.contentId = ?")
    Article findByContentId(String contentId);

    @Query(value = "select a from Article a where " +
    		"exists(from Article ea left join ea.allRights ar where ar in(select r from Right r join r.roles rt where rt = ? and r.right like 'article.view%') and a = ea) " +
					"order by a.modifiedAt desc", limitClause = true)
    List<Article> findAllArticlesForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult, Integer maxResult);

    @Query("select count(a) from Article a where a.contentId like ?")
    long existsContentId(String contentId);
}
