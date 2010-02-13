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
package org.devproof.portal.module.article.dao;

import java.util.List;

import org.devproof.portal.core.module.common.annotation.Cache;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;

/**
 * @author Carsten Hufe
 */
@Cache(region = ArticleConstants.QUERY_CACHE_REGION)
public interface ArticleDao extends GenericDao<ArticleEntity, Integer> {
	@Query("select a.allRights from ArticleEntity a where a.modifiedBy = (select max(modifiedBy) from ArticleEntity)")
	List<RightEntity> findLastSelectedRights();

	@Query("select a from ArticleEntity a left join fetch a.articlePages where a.id = ?")
	ArticleEntity findByIdAndPrefetch(Integer id);

	@Query("select a from ArticleEntity a where a.contentId = ?")
	ArticleEntity findByContentId(String contentId);

	@Query(value = "select distinct(a) from ArticleEntity a join a.allRights vr"
			+ " where vr in (select rt from RoleEntity r join r.rights rt where r = ? and rt.right like 'article.view%') order by a.modifiedAt desc", limitClause = true)
	List<ArticleEntity> findAllArticlesForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult, Integer maxResult);

	@Query("select count(a) from ArticleEntity a where a.contentId like ?")
	long existsContentId(String contentId);
}
