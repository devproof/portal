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
package org.devproof.portal.core.module.box.dao;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;

import java.util.List;

/**
 * @author Carsten Hufe
 */

@GenericRepository("boxDao")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
public interface BoxDao extends GenericDao<BoxEntity, Integer> {
    @Query("select max(b.sort) from BoxEntity b")
    Integer getMaxSortNum();

    @Query("select b from BoxEntity b where b.sort = ?")
    BoxEntity findBoxBySort(Integer sort);

    @Query("select b from BoxEntity b order by b.sort")
    List<BoxEntity> findAllOrderedBySort();
}
