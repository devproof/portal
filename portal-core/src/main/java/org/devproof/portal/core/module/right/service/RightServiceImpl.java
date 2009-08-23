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
package org.devproof.portal.core.module.right.service;

import java.util.List;

import org.devproof.portal.core.module.right.dao.RightDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class RightServiceImpl implements RightService {
	private List<RightEntity> allRights;
	private long dirtyTime = 0l;
	private RightDao rightDao;

	public void init() {
		refreshGlobalApplicationRights();
	}

	@Override
	public List<RightEntity> getAllRights() {
		return allRights;
	}

	@Override
	public long getDirtyTime() {
		return dirtyTime;
	}

	@Override
	public void refreshGlobalApplicationRights() {
		allRights = rightDao.findAll();
		dirtyTime = System.currentTimeMillis();
	}

	@Override
	public List<RightEntity> findAllOrderByDescription() {
		return rightDao.findAllOrderByDescription();
	}

	@Override
	public List<RightEntity> findRightsStartingWith(final String prefix) {
		return rightDao.findRightsStartingWith(prefix);
	}

	@Override
	public RightEntity newRightEntity() {
		return new RightEntity();
	}

	@Override
	public RightEntity newRightEntity(final String right) {
		return new RightEntity(right);
	}

	@Override
	public void delete(final RightEntity entity) {
		rightDao.delete(entity);
	}

	@Override
	public List<RightEntity> findAll() {
		return rightDao.findAll();
	}

	@Override
	public RightEntity findById(final String id) {
		return rightDao.findById(id);
	}

	@Override
	public void save(final RightEntity entity) {
		rightDao.save(entity);
	}

	@Required
	public void setRightDao(final RightDao rightDao) {
		this.rightDao = rightDao;
	}
}
