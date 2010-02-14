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

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.download.dao.DownloadDao;
import org.devproof.portal.module.download.entity.DownloadEntity;
import org.devproof.portal.module.download.entity.DownloadTagEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class DownloadServiceImpl implements DownloadService {
	private DownloadDao downloadDao;
	private TagService<DownloadTagEntity> downloadTagService;

	@Override
	public List<DownloadEntity> findAllDownloadsForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult,
			Integer maxResult) {
		return downloadDao.findAllDownloadsForRoleOrderedByDateDesc(role, firstResult, maxResult);
	}

	@Override
	public void incrementHits(DownloadEntity download) {
		downloadDao.incrementHits(download);
	}

	@Override
	public void markBrokenDownload(DownloadEntity download) {
		downloadDao.markBrokenDownload(download);
	}

	@Override
	public void markValidDownload(DownloadEntity download) {
		downloadDao.markValidDownload(download);
	}

	@Override
	public DownloadEntity newDownloadEntity() {
		DownloadEntity download = new DownloadEntity();
		download.setAllRights(downloadDao.findLastSelectedRights());
		return download;
	}

	@Override
	public void rateDownload(Integer rating, DownloadEntity download) {
		downloadDao.rateDownload(rating, download);
		downloadDao.refresh(download);
	}

	@Override
	public void delete(DownloadEntity entity) {
		downloadDao.delete(entity);
		downloadTagService.deleteUnusedTags();
	}

	@Override
	public List<DownloadEntity> findAll() {
		return downloadDao.findAll();
	}

	@Override
	public DownloadEntity findById(Integer id) {
		return downloadDao.findById(id);
	}

	@Override
	public void save(DownloadEntity entity) {
		downloadDao.save(entity);
		downloadTagService.deleteUnusedTags();
	}

	@Required
	public void setDownloadDao(DownloadDao downloadDao) {
		this.downloadDao = downloadDao;
	}

	@Required
	public void setDownloadTagService(TagService<DownloadTagEntity> downloadTagService) {
		this.downloadTagService = downloadTagService;
	}
}
