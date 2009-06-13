/*
 * Copyright 2009 Carsten Hufe devproof.org
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
package org.devproof.portal.module.otherpage.service;

import java.util.List;

import org.devproof.portal.module.otherpage.dao.OtherPageDao;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;

/**
 * @author Carsten Hufe
 */
public class OtherPageServiceImpl implements OtherPageService {
	private OtherPageDao otherPageDao;

	@Override
	public boolean existsContentId(final String contentId) {
		return this.otherPageDao.existsContentId(contentId) > 0;
	}

	@Override
	public OtherPageEntity findOtherPageByContentId(final String contentId) {
		return this.otherPageDao.findOtherPageByContentId(contentId);
	}

	@Override
	public OtherPageEntity newOtherPageEntity() {
		return new OtherPageEntity();
	}

	@Override
	public void delete(final OtherPageEntity entity) {
		this.otherPageDao.delete(entity);
	}

	@Override
	public List<OtherPageEntity> findAll() {
		return this.otherPageDao.findAll();
	}

	@Override
	public OtherPageEntity findById(final Integer id) {
		return this.otherPageDao.findById(id);
	}

	@Override
	public void save(final OtherPageEntity entity) {
		this.otherPageDao.save(entity);
	}

	public void setOtherPageDao(final OtherPageDao otherPageDao) {
		this.otherPageDao = otherPageDao;
	}
}
