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
package org.devproof.portal.module.download.service;

import java.util.List;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.download.entity.DownloadEntity;

/**
 * @author Carsten Hufe
 */
public interface DownloadService extends CrudService<DownloadEntity, Integer> {
	/**
	 * Returns a new instance of {@link DownloadEntity}
	 * 
	 * @return new instance of {@link DownloadEntity}
	 */
	DownloadEntity newDownloadEntity();

	/**
	 * Finds all downloads filtered by role
	 * 
	 * @param role
	 *            {@link RoleEntity} to filter
	 * @param firstResult
	 *            first result
	 * @param maxResult
	 *            maximum result
	 * @return list with downloads
	 */
	List<DownloadEntity> findAllDownloadsForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult,
			Integer maxResult);

	/**
	 * Increments the number of downloads by one
	 * 
	 * @param download
	 *            {@link DownloadEntity}to increment
	 */
	void incrementHits(DownloadEntity download);

	/**
	 * Rates a {@link DownloadEntity}
	 * 
	 * @param rating
	 *            a value from 1 to 5
	 * @param download
	 *            the download to rate
	 */
	void rateDownload(Integer rating, DownloadEntity download);

	/**
	 * Marks a download as broken
	 * 
	 * @param download
	 *            {@link DownloadEntity} to mark
	 */
	void markBrokenDownload(DownloadEntity download);

	/**
	 * Marks a download as valid
	 * 
	 * @param download
	 *            {@link DownloadEntity} to mark
	 */
	void markValidDownload(DownloadEntity download);
}
