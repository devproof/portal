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
package org.devproof.portal.module.blog.service;

import java.util.List;

import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.blog.dao.BlogDao;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.entity.BlogTagEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class BlogServiceImpl implements BlogService {
	private BlogDao blogDao;
	private TagService<BlogTagEntity> blogTagService;

	@Override
	public void delete(final BlogEntity entity) {
		blogDao.delete(entity);
		blogTagService.deleteUnusedTags();
	}

	@Override
	public List<BlogEntity> findAll() {
		return blogDao.findAll();
	}

	@Override
	public BlogEntity findById(final Integer id) {
		return blogDao.findById(id);
	}

	@Override
	public void save(final BlogEntity entity) {
		blogDao.save(entity);
		blogTagService.deleteUnusedTags();
	}

	@Override
	public BlogEntity newBlogEntity() {
		return new BlogEntity();
	}

	@Required
	public void setBlogDao(final BlogDao blogDao) {
		this.blogDao = blogDao;
	}

	@Required
	public void setBlogTagService(final TagService<BlogTagEntity> blogTagService) {
		this.blogTagService = blogTagService;
	}
}
