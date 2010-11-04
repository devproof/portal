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

import org.devproof.portal.module.blog.repository.BlogRepository;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Carsten Hufe
 */
@Service("blogService")
public class BlogServiceImpl implements BlogService {
    private BlogRepository blogRepository;
    private BlogTagService blogTagService;

    @Override
    public void delete(BlogEntity entity) {
        blogRepository.delete(entity);
        blogTagService.deleteUnusedTags();
    }

    @Override
    public BlogEntity findById(Integer id) {
        return blogRepository.findById(id);
    }

    @Override
    public void save(BlogEntity entity) {
        blogRepository.save(entity);
        blogTagService.deleteUnusedTags();
    }

    @Override
    public BlogEntity newBlogEntity() {
        BlogEntity blog = new BlogEntity();
        blog.setAllRights(blogRepository.findLastSelectedRights());
        return blog;
    }

    @Autowired
    public void setBlogDao(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Autowired
    public void setBlogTagService(BlogTagService blogTagService) {
        this.blogTagService = blogTagService;
    }
}
