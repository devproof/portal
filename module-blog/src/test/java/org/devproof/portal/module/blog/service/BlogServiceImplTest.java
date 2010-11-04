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

import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.repository.BlogRepository;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Carsten Hufe
 */
public class BlogServiceImplTest {
    private BlogServiceImpl impl;
    private BlogRepository mock;
    private BlogTagService mockTag;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mock = createStrictMock(BlogRepository.class);
        mockTag = createStrictMock(BlogTagService.class);
        impl = new BlogServiceImpl();
        impl.setBlogRepository(mock);
        impl.setBlogTagService(mockTag);
    }

    @Test
    public void testSave() {
        Blog e = createBlogEntity();
        expect(mock.save(e)).andReturn(e);
        mockTag.deleteUnusedTags();
        replay(mock);
        replay(mockTag);
        impl.save(e);
        verify(mock);
        verify(mockTag);
    }

    @Test
    public void testDelete() {
        Blog e = createBlogEntity();
        mock.delete(e);
        mockTag.deleteUnusedTags();
        replay(mock);
        replay(mockTag);
        impl.delete(e);
        verify(mock);
        verify(mockTag);
    }

    @Test
    public void testFindById() {
        Blog e = createBlogEntity();
        expect(mock.findById(1)).andReturn(e);
        replay(mock);
        assertEquals(impl.findById(1), e);
        verify(mock);
    }

    @Test
    public void testNewBlogEntity() {
        assertNotNull(impl.newBlogEntity());
    }

    private Blog createBlogEntity() {
        Blog blog = new Blog();
        blog.setId(1);
        return blog;
    }
}
