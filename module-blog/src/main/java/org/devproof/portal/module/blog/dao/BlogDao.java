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
package org.devproof.portal.module.blog.dao;

import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.BlogEntity;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@CacheQuery(region = BlogConstants.QUERY_CACHE_REGION)
public interface BlogDao extends GenericDao<BlogEntity, Integer> {
	@CacheQuery(enabled = false)
	@Query("select b.allRights from BlogEntity b where b.modifiedAt = (select max(modifiedAt) from BlogEntity)")
	List<RightEntity> findLastSelectedRights();
}
