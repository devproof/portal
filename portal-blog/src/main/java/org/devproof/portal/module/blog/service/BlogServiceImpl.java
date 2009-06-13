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

/**
 * @author Carsten Hufe
 */
public class BlogServiceImpl implements BlogService {
	private BlogDao blogDao;
	private TagService<BlogTagEntity> blogTagService;

	@Override
	public void delete(final BlogEntity entity) {
		this.blogDao.delete(entity);
		this.blogTagService.deleteUnusedTags();
	}

	@Override
	public List<BlogEntity> findAll() {
		return this.blogDao.findAll();
	}

	@Override
	public BlogEntity findById(final Integer id) {
		return this.blogDao.findById(id);
	}

	@Override
	public void save(final BlogEntity entity) {
		this.blogDao.save(entity);
		this.blogTagService.deleteUnusedTags();
	}

	@Override
	public BlogEntity newBlogEntity() {
		return new BlogEntity();
	}

	public void setBlogDao(final BlogDao blogDao) {
		this.blogDao = blogDao;
	}

	public void setBlogTagService(final TagService<BlogTagEntity> blogTagService) {
		this.blogTagService = blogTagService;
	}
}
