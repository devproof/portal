/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devproof.portal.module.download.mount;

import org.apache.wicket.PageParameters;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.service.MountService;
import org.devproof.portal.module.download.DownloadConstants;
import org.devproof.portal.module.download.page.DownloadPage;
import org.devproof.portal.module.download.page.DownloadRedirectPage;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
public class DownloadMountHandlerTest {
    private DownloadMountHandler impl;
    private MountService mockMountService;

    @Before
    public void setUp() throws Exception {
        mockMountService = createStrictMock(MountService.class);
        impl = new DownloadMountHandler();
        impl.setMountService(mockMountService);
    }

    @Test
    public void testGetRequestTarget() throws Exception {
        MountPoint mp = new MountPoint();
        mp.setId(1);
        mp.setMountPath("/hello");
        mp.setDefaultUrl(true);
        mp.setRelatedContentId("123");
        BookmarkablePageRequestTarget requestTarget = (BookmarkablePageRequestTarget) impl.getRequestTarget("/hello", mp);
        assertEquals(DownloadPage.class, requestTarget.getPageClass());
    }

    @Test
    public void testCanHandlePageClass_notfound() throws Exception {
        expect(mockMountService.existsMountPoint("123", DownloadConstants.HANDLER_KEY)).andReturn(false);
        replay(mockMountService);
        assertFalse(impl.canHandlePageClass(DownloadPage.class, new PageParameters("id=123")));
        verify(mockMountService);
    }

    @Test
    public void testCanHandlePageClass_read() throws Exception {
        expect(mockMountService.existsMountPoint("123", DownloadConstants.HANDLER_KEY)).andReturn(true);
        replay(mockMountService);
        assertTrue(impl.canHandlePageClass(DownloadPage.class, new PageParameters("id=123")));
        verify(mockMountService);
    }

    @Test
    public void testUrlFor_null() throws Exception {
        expect(mockMountService.findDefaultMountPoint("123", DownloadConstants.HANDLER_KEY)).andReturn(null);
        replay(mockMountService);
        assertNull(impl.urlFor(DownloadPage.class, new PageParameters("id=123")));
        verify(mockMountService);
    }

    @Test
    public void testUrlFor_read() throws Exception {
        MountPoint mp = new MountPoint();
        mp.setId(1);
        mp.setMountPath("/hello");
        mp.setDefaultUrl(true);
        mp.setRelatedContentId("123");
        expect(mockMountService.findDefaultMountPoint("123", DownloadConstants.HANDLER_KEY)).andReturn(mp);
        replay(mockMountService);
        assertEquals("/hello", impl.urlFor(DownloadPage.class, new PageParameters("id=123")));
        verify(mockMountService);
    }

    @Test
    public void testGetHandlerKey() throws Exception {
        assertEquals(DownloadConstants.HANDLER_KEY, impl.getHandlerKey());
    }


    @Test
    public void testCanHandlePageClass_download() throws Exception {
        expect(mockMountService.existsMountPoint("123", DownloadConstants.HANDLER_KEY)).andReturn(true);
        replay(mockMountService);
        assertTrue(impl.canHandlePageClass(DownloadRedirectPage.class, new PageParameters("0=123")));
        verify(mockMountService);
    }


    @Test
    public void testUrlFor_visit() throws Exception {
        MountPoint mp = new MountPoint();
        mp.setId(1);
        mp.setMountPath("/hello");
        mp.setDefaultUrl(true);
        mp.setRelatedContentId("123");
        expect(mockMountService.findDefaultMountPoint("123", DownloadConstants.HANDLER_KEY)).andReturn(mp);
        replay(mockMountService);
        assertEquals("/hello/download", impl.urlFor(DownloadRedirectPage.class, new PageParameters("0=123")));
        verify(mockMountService);
    }
}
