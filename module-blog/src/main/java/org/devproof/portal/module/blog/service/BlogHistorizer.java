package org.devproof.portal.module.blog.service;

import org.devproof.portal.core.module.historization.interceptor.Action;
import org.devproof.portal.core.module.historization.interceptor.Historizer;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogHistorized;
import org.devproof.portal.module.blog.repository.BlogHistorizedRepository;
import org.devproof.portal.module.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

/**
 * Historizer for Blog
 *
 * @author Carsten Hufe
 */
@Component
// TODO unit test
public class BlogHistorizer implements Historizer<Blog, BlogHistorized> {
    private BlogTagService blogTagService;
    private RightService rightService;
    private BlogHistorizedRepository blogHistorizedRepository;
    private BlogRepository blogRepository;

    @Override
    public void historize(Blog blog, Action action) {
        BlogHistorized historized = new BlogHistorized();
        historized.copyFrom(blog);
        historized.setTags(blogTagService.convertTagsToWhitespaceSeparated(blog.getTags()));
        historized.setRights(rightService.convertRightsToWhitespaceSeparated(blog.getAllRights()));
        historized.setBlog(blog);
        historized.setAction(action);
        blogHistorizedRepository.save(historized);
    }

    @Override
    public Blog restore(BlogHistorized historized) {
        Blog blog = historized.getBlog();
        blog.copyFrom(historized);
        blog.setAllRights(rightService.findWhitespaceSeparatedRights(historized.getTags()));
        blog.setTags(blogTagService.findWhitespaceSeparatedTagsAndCreateIfNotExists(historized.getTags()));
        blogRepository.save(blog);
        return blog;
    }

    @Override
    public void deleteHistory(Blog blog) {
        blogHistorizedRepository.deleteHistoryForBlog(blog);
    }

    @Autowired
    public void setRightService(RightService rightService) {
        this.rightService = rightService;
    }

    @Autowired
    public void setBlogTagService(BlogTagService blogTagService) {
        this.blogTagService = blogTagService;
    }

    @Autowired
    public void setBlogHistorizedRepository(BlogHistorizedRepository blogHistorizedRepository) {
        this.blogHistorizedRepository = blogHistorizedRepository;
    }

    @Autowired
    public void setBlogRepository(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }
}
