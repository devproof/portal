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
package org.devproof.portal.module.article.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleHistorized;

/**
 * @author Carsten Hufe
 */
@GenericRepository("articleHistorizedRepository")
public interface ArticleHistorizedRepository extends CrudRepository<ArticleHistorized, Integer> {
    @Query("select max(a.versionNumber) from ArticleHistorized a where a.article = ?")
    Integer findLastVersionNumber(Article article);
    
    @BulkUpdate("delete from ArticleHistorized where article = ?")
    void deleteHistoryForArticle(Article article);
}
