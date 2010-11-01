package org.devproof.portal.module.blog.service;

import org.devproof.portal.core.module.tag.service.TagServiceImpl;
import org.devproof.portal.module.blog.entity.BlogTagEntity;
import org.springframework.stereotype.Service;

/**
 * @author Carsten Hufe
 */
@Service("blogTagService")
public class BlogTagServiceImpl extends TagServiceImpl<BlogTagEntity> implements BlogTagService {
    @Override
    public String getRelatedTagRight() {
        return "blog.view";
    }
}
