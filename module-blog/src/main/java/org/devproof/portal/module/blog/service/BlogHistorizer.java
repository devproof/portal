package org.devproof.portal.module.blog.service;

import org.devproof.portal.core.module.historization.interceptor.Historizer;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogHistorized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Override
    public BlogHistorized historize(Blog blog) {
        BlogHistorized historized = new BlogHistorized();
        historized.copyFrom(blog);
        historized.setTags(blogTagService.convertTagsToWhitespaceSeparated(blog.getTags()));
        historized.setRights(rightService.convertRightsToWhitespaceSeparated(blog.getAllRights()));
        // TODO set back reference
        return historized;
    }

    @Override
    public void restore(BlogHistorized blogHistorized, Blog blog) {
        
    }

    @Autowired
    public void setRightService(RightService rightService) {
        this.rightService = rightService;
    }

    @Autowired
    public void setBlogTagService(BlogTagService blogTagService) {
        this.blogTagService = blogTagService;
    }
}
