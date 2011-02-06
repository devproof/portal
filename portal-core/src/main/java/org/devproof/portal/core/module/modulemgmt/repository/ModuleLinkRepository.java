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
package org.devproof.portal.core.module.modulemgmt.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink.LinkType;

import java.util.List;

/**
 * Accessing module links
 *
 * @author Carsten Hufe
 */
@GenericRepository("moduleLinkRepository")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
public interface ModuleLinkRepository extends CrudRepository<ModuleLink, Integer> {
    @Query("select m from ModuleLink m where m.linkType = ? order by m.sort")
    List<ModuleLink> findModuleLinks(LinkType type);

    @Query("select m from ModuleLink m where m.linkType = ? and m.visible = true order by m.sort")
    List<ModuleLink> findVisibleModuleLinks(LinkType type);

    @Query("select max(m.sort) from ModuleLink m where m.linkType = ?")
    Integer getMaxSortNum(LinkType type);

    @Query("select m from ModuleLink m where m.linkType = ? and m.sort = ?")
    ModuleLink findModuleLinkBySort(LinkType linkType, Integer sort);
}
