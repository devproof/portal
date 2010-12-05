package org.devproof.portal.module.blog.service;

import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.historization.service.Historizer;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogHistorized;
import org.devproof.portal.module.blog.repository.BlogHistorizedRepository;
import org.devproof.portal.module.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
        historize(blog, action, null);
    }

    private void historize(Blog blog, Action action, Integer restoredVersion) {
        BlogHistorized historized = new BlogHistorized();
        historized.copyFrom(blog);
        historized.setTags(blogTagService.convertTagsToWhitespaceSeparated(blog.getTags()));
        historized.setRights(rightService.convertRightsToWhitespaceSeparated(blog.getAllRights()));
        historized.setBlog(blog);
        historized.setAction(action);
        historized.setActionAt(new Date());
        historized.setVersionNumber(retrieveNextVersionNumber(blog));
        historized.setRestoredFromVersion(restoredVersion);
        blogHistorizedRepository.save(historized);
    }

    private Integer retrieveNextVersionNumber(Blog blog) {
        Integer nextNumber = blogHistorizedRepository.findLastVersionNumber(blog);
        if(nextNumber == null) {
            nextNumber = 0;
        }
        return nextNumber + 1;
    }

    @Override
    public Blog restore(BlogHistorized historized) {
        Blog blog = historized.getBlog();
        blog.copyFrom(historized);
        blog.setAllRights(rightService.findWhitespaceSeparatedRights(historized.getTags()));
        blog.setTags(blogTagService.findWhitespaceSeparatedTagsAndCreateIfNotExists(historized.getTags()));
        blog.setUpdateModificationData(false);
        historize(blog, Action.RESTORED, historized.getVersionNumber());
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
