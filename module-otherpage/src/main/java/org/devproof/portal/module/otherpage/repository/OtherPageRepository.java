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
package org.devproof.portal.module.otherpage.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.otherpage.OtherPageConstants;
import org.devproof.portal.module.otherpage.entity.OtherPage;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@GenericRepository("otherPageDao")
@CacheQuery(region = OtherPageConstants.QUERY_CACHE_REGION)
public interface OtherPageRepository extends CrudRepository<OtherPage, Integer> {
    @CacheQuery(enabled = false)
    @Query("select op.allRights from OtherPage op where op.modifiedAt = (select max(modifiedAt) from OtherPage)")
    List<RightEntity> findLastSelectedRights();

    @Query("select count(op.contentId) from OtherPage op where op.contentId like ?")
    long existsContentId(String contentId);

    @Query("select op from OtherPage op where op.contentId like ?")
    OtherPage findOtherPageByContentId(String contentId);
}
