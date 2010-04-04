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
package org.devproof.portal.core.module.modulemgmt.dao;

import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;

import java.util.List;

/**
 * Accessing module links
 * 
 * @author Carsten Hufe
 * 
 */
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
public interface ModuleLinkDao extends GenericDao<ModuleLinkEntity, Integer> {
	@Query("select m from ModuleLinkEntity m where m.linkType = ? order by m.sort")
	List<ModuleLinkEntity> findModuleLinks(LinkType type);

	@Query("select m from ModuleLinkEntity m where m.linkType = ? and m.visible = true order by m.sort")
	List<ModuleLinkEntity> findVisibleModuleLinks(LinkType type);

	@Query("select max(m.sort) from ModuleLinkEntity m where m.linkType = ?")
	Integer getMaxSortNum(LinkType type);

	@Query("select m from ModuleLinkEntity m where m.linkType = ? and m.sort = ?")
	ModuleLinkEntity findModuleLinkBySort(LinkType linkType, Integer sort);
}
