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
package org.devproof.portal.core.module.right.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.core.module.right.entity.Right;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@GenericRepository("rightRepository")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
public interface RightRepository extends CrudRepository<Right, String> {
    @Query("select r from Right r")
    List<Right> findAll();

    @Query("select r from Right r where r.right like ?||'.%'")
    List<Right> findRightsStartingWith(String prefix);

    @Query("select r from Right r order by r.description asc")
    List<Right> findAllOrderByDescription();
}
