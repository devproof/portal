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
package org.devproof.portal.core.module.tag.service;

import java.util.List;

import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.dao.TagDao;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;

/**
 * @author Carsten Hufe
 */
public class TagServiceImpl<T extends BaseTagEntity<?>> implements TagService<T> {
	private TagDao<T> tagDao;
	private String relatedTagRight;

	@Override
	public void deleteUnusedTags() {
		this.tagDao.deleteUnusedTags();
	}

	@Override
	public List<T> findMostPopularTags(final Integer firstResult, final Integer maxResult) {
		return this.tagDao.findMostPopularTags(firstResult, maxResult);
	}

	@Override
	public List<T> findMostPopularTags(final RoleEntity role, final Integer firstResult, final Integer maxResult) {
		return this.tagDao.findMostPopularTags(role, this.relatedTagRight, firstResult, maxResult);
	}

	@Override
	public List<T> findTagsStartingWith(final String prefix) {
		return this.tagDao.findTagsStartingWith(prefix);
	}

	@Override
	public T newTagEntity(final String tag) {
		T obj;
		try {
			obj = this.tagDao.getType().newInstance();
		} catch (final InstantiationException e) {
			throw new UnhandledException(e);
		} catch (final IllegalAccessException e) {
			throw new UnhandledException(e);
		}
		obj.setTagname(tag);
		return obj;
	}

	@Override
	public String getRelatedTagRight() {
		return this.relatedTagRight;
	}

	@Override
	public void delete(final T entity) {
		this.tagDao.delete(entity);
	}

	@Override
	public List<T> findAll() {
		return this.tagDao.findAll();
	}

	@Override
	public T findById(final String id) {
		return this.tagDao.findById(id);
	}

	@Override
	public void save(final T entity) {
		this.tagDao.save(entity);
	}

	public void setTagDao(final TagDao<T> tagDao) {
		this.tagDao = tagDao;
	}

	public void setRelatedTagRight(final String relatedTagRight) {
		this.relatedTagRight = relatedTagRight;
	}
}
