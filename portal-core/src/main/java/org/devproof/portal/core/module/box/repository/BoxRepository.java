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
package org.devproof.portal.core.module.box.repository;

import org.devproof.portal.core.module.box.entity.Box;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.GenericRepository;

import java.util.List;

/**
 * Generic Dao for the boxes
 *
 * @author Carsten Hufe
 */
@org.devproof.portal.core.config.GenericRepository("boxRepository")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
public interface BoxRepository extends GenericRepository<Box, Integer> {
    @Query("select max(b.sort) from Box b")
    Integer getMaxSortNum();

    @Query("select b from Box b where b.sort = ?")
    Box findBoxBySort(Integer sort);

    @Query("select b from Box b order by b.sort")
    List<Box> findAllOrderedBySort();
}
