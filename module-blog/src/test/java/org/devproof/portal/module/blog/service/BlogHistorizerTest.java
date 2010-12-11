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

import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.entity.BlogHistorized;
import org.devproof.portal.module.blog.entity.BlogTag;
import org.devproof.portal.module.blog.repository.BlogHistorizedRepository;
import org.devproof.portal.module.blog.repository.BlogRepository;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;

/**
 * @author Carsten Hufe
 */
public class BlogHistorizerTest {
    private BlogHistorizer impl;
    private BlogRepository mockRepo;
    private BlogTagService mockTagService;
    private RightService mockRightService;
    private BlogHistorizedRepository mockHistorizedRepo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mockRepo = createStrictMock(BlogRepository.class);
        mockTagService = createStrictMock(BlogTagService.class);
        mockRightService = createStrictMock(RightService.class);
        mockHistorizedRepo = createStrictMock(BlogHistorizedRepository.class);
        impl = new BlogHistorizer();
        impl.setBlogRepository(mockRepo);
        impl.setRightService(mockRightService);
        impl.setBlogHistorizedRepository(mockHistorizedRepo);
        impl.setBlogTagService(mockTagService);
    }


    @Test
    public void testHistorize() throws Exception {
        expect(mockTagService.convertTagsToWhitespaceSeparated(EasyMock.<List<BlogTag>>anyObject())).andReturn("tag1 tag2");
        expect(mockRightService.convertRightsToWhitespaceSeparated(EasyMock.<List<Right>>anyObject())).andReturn("right1 right2");
        expect(mockHistorizedRepo.findLastVersionNumber(anyObject(Blog.class))).andReturn(1);
        expect(mockHistorizedRepo.save(EasyMock.<BlogHistorized>anyObject())).andReturn(null);
        replay(mockTagService, mockRightService, mockHistorizedRepo);
        Blog blog = new Blog();
        blog.setId(1);
        blog.setHeadline("headline");
        blog.setContent("content");
        impl.historize(blog, Action.CREATED);
        verify(mockTagService, mockRightService, mockHistorizedRepo);
    }

    @Test
    public void testRestore() throws Exception {
        expect(mockRightService.findWhitespaceSeparatedRights(anyObject(String.class))).andReturn(new ArrayList<Right>());
        expect(mockTagService.findWhitespaceSeparatedTagsAndCreateIfNotExists(anyObject(String.class))).andReturn(new ArrayList<BlogTag>());
        // historize again
        expect(mockTagService.convertTagsToWhitespaceSeparated(EasyMock.<List<BlogTag>>anyObject())).andReturn("tag1 tag2");
        expect(mockRightService.convertRightsToWhitespaceSeparated(EasyMock.<List<Right>>anyObject())).andReturn("right1 right2");
        expect(mockHistorizedRepo.findLastVersionNumber(anyObject(Blog.class))).andReturn(1);
        expect(mockHistorizedRepo.save(EasyMock.<BlogHistorized>anyObject())).andReturn(null);
        // restore
        expect(mockRepo.save(anyObject(Blog.class))).andReturn(new Blog());
        replay(mockTagService, mockRightService, mockHistorizedRepo, mockRepo);
        BlogHistorized historized = new BlogHistorized();
        historized.setBlog(new Blog());
        Blog restoredBlog = impl.restore(historized);
        assertFalse(restoredBlog.isUpdateModificationData());
        verify(mockTagService, mockRightService, mockHistorizedRepo, mockRepo);
    }

    @Test
    public void testDeleteHistory() throws Exception {
        mockHistorizedRepo.deleteHistoryForBlog(anyObject(Blog.class));
        replay(mockHistorizedRepo);
        impl.deleteHistory(new Blog());
        verify(mockHistorizedRepo);
    }
}
