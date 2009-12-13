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
package org.devproof.portal.core.module.tag.service;

import java.util.List;

import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.dao.TagDao;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class TagServiceImpl<T extends BaseTagEntity<?>> implements TagService<T> {
	private TagDao<T> tagDao;
	private String relatedTagRight;

	@Override
	public void deleteUnusedTags() {
		tagDao.deleteUnusedTags();
	}

	@Override
	public List<T> findMostPopularTags(Integer firstResult, Integer maxResult) {
		return tagDao.findMostPopularTags(firstResult, maxResult);
	}

	@Override
	public List<T> findMostPopularTags(RoleEntity role, Integer firstResult, Integer maxResult) {
		return tagDao.findMostPopularTags(role, relatedTagRight, firstResult, maxResult);
	}

	@Override
	public List<T> findTagsStartingWith(String prefix) {
		return tagDao.findTagsStartingWith(prefix);
	}

	@Override
	public T newTagEntity(String tag) {
		T obj;
		try {
			obj = tagDao.getType().newInstance();
		} catch (InstantiationException e) {
			throw new UnhandledException(e);
		} catch (IllegalAccessException e) {
			throw new UnhandledException(e);
		}
		obj.setTagname(tag);
		return obj;
	}

	@Override
	public String getRelatedTagRight() {
		return relatedTagRight;
	}

	@Override
	public void delete(T entity) {
		tagDao.delete(entity);
	}

	@Override
	public List<T> findAll() {
		return tagDao.findAll();
	}

	@Override
	public T findById(String id) {
		return tagDao.findById(id);
	}

	@Override
	public void save(T entity) {
		tagDao.save(entity);
	}

	@Override
	public T findByIdAndCreateIfNotExists(String tagName) {
		T tag = findById(tagName);
		if (tag == null) {
			tag = newTagEntity(tagName);
			save(tag);
		}
		return tag;
	}

	@Required
	public void setTagDao(TagDao<T> tagDao) {
		this.tagDao = tagDao;
	}

	@Required
	public void setRelatedTagRight(String relatedTagRight) {
		this.relatedTagRight = relatedTagRight;
	}
}
