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
package org.devproof.portal.module.download.dao;

import java.util.List;

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.download.entity.DownloadEntity;

/**
 * @author Carsten Hufe
 */
public interface DownloadDao extends GenericDao<DownloadEntity, Integer> {
	@Query("select d.allRights from DownloadEntity d where d.modifiedBy = (select max(modifiedBy) from DownloadEntity)")
	List<RightEntity> findLastSelectedRights();

	@Query(value = "select distinct(d) from DownloadEntity d join d.allRights ar"
			+ " where ar in (select rt from RoleEntity r join r.rights rt where r = ? and rt.right like 'download.view%') order by d.modifiedAt desc", limitClause = true)
	List<DownloadEntity> findAllDownloadsForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult,
			Integer maxResult);

	@BulkUpdate("update DownloadEntity d set d.hits = (d.hits + 1) where d = ?")
	void incrementHits(DownloadEntity download);

	@BulkUpdate("update DownloadEntity d set d.numberOfVotes = (d.numberOfVotes + 1), d.sumOfRating = (d.sumOfRating + ?) where d = ?")
	void rateDownload(Integer rating, DownloadEntity download);

	@BulkUpdate("update DownloadEntity d set d.broken = true where d = ?")
	void markBrokenDownload(DownloadEntity download);

	@BulkUpdate("update DownloadEntity d set d.broken = false where d = ?")
	void markValidDownload(DownloadEntity download);
}
