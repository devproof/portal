/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.blog.service;

import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.blog.dao.BlogDao;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.entity.BlogTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

/**
 * @author Carsten Hufe
 */
@Service("blogService")
public class BlogServiceImpl implements BlogService {
    private BlogDao blogDao;
    private BlogTagService blogTagService;

    @Override
    public void delete(BlogEntity entity) {
        blogDao.delete(entity);
        blogTagService.deleteUnusedTags();
    }

    @Override
    public BlogEntity findById(Integer id) {
        return blogDao.findById(id);
    }

    @Override
    public void save(BlogEntity entity) {
        blogDao.save(entity);
        blogTagService.deleteUnusedTags();
    }

    @Override
    public BlogEntity newBlogEntity() {
        BlogEntity blog = new BlogEntity();
        blog.setAllRights(blogDao.findLastSelectedRights());
        return blog;
    }

    @Autowired
    public void setBlogDao(BlogDao blogDao) {
        this.blogDao = blogDao;
    }

    @Autowired
    public void setBlogTagService(BlogTagService blogTagService) {
        this.blogTagService = blogTagService;
    }
}
