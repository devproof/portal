/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.mount.service;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.request.handler.PageProvider;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.module.common.page.NoStartPage;
import org.devproof.portal.core.module.feed.DummyPage;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.core.module.mount.registry.MountHandlerRegistry;
import org.devproof.portal.core.module.mount.repository.MountPointRepository;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.*;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
public class MountServiceImplTest {
    private MountServiceImpl impl;
    private MountHandlerRegistry mockRegistry;
    private MountPointRepository mockRepository;

    @Before
    public void setUp() throws Exception {
        mockRegistry = createStrictMock(MountHandlerRegistry.class);
        mockRepository = createStrictMock(MountPointRepository.class);
        impl = new MountServiceImpl();
        impl.setMountHandlerRegistry(mockRegistry);
        impl.setMountPointRepository(mockRepository);
    }

    @Test
    public void testFindMountPointsStartingWith() throws Exception {
        List<String> expected = Arrays.asList("/hello1", "/hello2");
        expect(mockRepository.findMountPointsStartingWith("/hello")).andReturn(expected);
        replay(mockRepository);
        List<String> mountPointsStartingWith = impl.findMountPointsStartingWith("/hello");
        assertEquals(expected, mountPointsStartingWith);
        verify(mockRepository);
    }

    @Test
    public void testDelete_mountPoint() throws Exception {
        MountPoint delete = createMountPoint(true);
        mockRepository.delete(delete);
        MountPoint expected = createMountPoint(false);
        expect(mockRepository.findMountPoints(delete.getRelatedContentId(), delete.getHandlerKey())).andReturn(Arrays.asList(expected));
        expect(mockRepository.save(expected)).andReturn(null);
        replay(mockRepository);
        impl.delete(delete);
        verify(mockRepository);

    }

    private MountPoint createMountPoint(boolean defaultUrl) {
        MountPoint mp = new MountPoint();
        mp.setId(1);
        mp.setDefaultUrl(defaultUrl);
        mp.setMountPath("/path");
        mp.setHandlerKey("dummy");
        mp.setRelatedContentId("123");
        return mp;
    }

    @Test
    public void testDeleteList() throws Exception {
        List<MountPoint> deletes = Arrays.asList(createMountPoint(true));
        MountPoint delete = deletes.get(0);
        mockRepository.delete(delete);
        MountPoint expected = createMountPoint(false);
        expect(mockRepository.findMountPoints(delete.getRelatedContentId(), delete.getHandlerKey())).andReturn(Arrays.asList(expected));
        expect(mockRepository.save(expected)).andReturn(null);
        replay(mockRepository);
        impl.delete(delete);
        verify(mockRepository);
    }

    @Test
    public void testDelete_contentId() throws Exception {
        List<MountPoint> deletes = Arrays.asList(createMountPoint(true));
        expect(mockRepository.findMountPoints("123", "article")).andReturn(deletes);
        MountPoint delete = deletes.get(0);
        mockRepository.delete(delete);
        MountPoint expected = createMountPoint(false);
        expect(mockRepository.findMountPoints(delete.getRelatedContentId(), delete.getHandlerKey())).andReturn(Arrays.asList(expected));
        expect(mockRepository.save(expected)).andReturn(null);
        replay(mockRepository);
        impl.delete("123", "article");
        verify(mockRepository);
    }

    @Test
    public void testSave_mountPoint() throws Exception {
        MountPoint expected = createMountPoint(true);
        expect(mockRepository.existsMountPoint("/path", "123", "dummy")).andReturn(0l);
        expect(mockRepository.save(expected)).andReturn(expected);
        replay(mockRepository);
        impl.save(expected);
        verify(mockRepository);
    }

    @Test
    public void testSaveList() throws Exception {
        MountPoint expected1 = createMountPoint(true);
        MountPoint expected2 = createMountPoint(true);
        expected2.setMountPath("");
        List<MountPoint> expecteds = Arrays.asList(expected1, expected2);
        expect(mockRepository.existsMountPoint("/path", "321", "dummy")).andReturn(0l);
        expect(mockRepository.save(expected1)).andReturn(expected1);
        mockRepository.delete(expected2);
        replay(mockRepository);
        impl.save(expecteds, "321");
        verify(mockRepository);

    }

    @Test
    public void testExistsMountPoint() throws Exception {
        expect(mockRepository.existsMountPoint("123", "article")).andReturn(1l);
        replay(mockRepository);
        assertTrue(impl.existsMountPoint("123", "article"));
        verify(mockRepository);
    }

    @Test
    public void testResolveRequestTarget() throws Exception {
        expect(mockRepository.findMountPointByUrl("/path")).andReturn(createMountPoint(true));
        expect(mockRegistry.isMountHandlerAvailable("dummy")).andReturn(true);
        expect(mockRegistry.getMountHandler("dummy")).andReturn(new DummyMountHandler());
        replay(mockRegistry, mockRepository);
        assertNotNull(impl.resolveRequestHandler(new DummyRequest()));
        verify(mockRegistry, mockRepository);
    }

    @Test
    public void testFindDefaultMountPoint() throws Exception {
        MountPoint expected = createMountPoint(true);
        expect(mockRepository.findDefaultMountPoint("123", "article")).andReturn(expected);
        replay(mockRepository);
        assertEquals(expected, impl.findDefaultMountPoint("123", "article"));
        verify(mockRepository);
    }

    @Test
    public void testResolveMountPoint() throws Exception {
        MountPoint expected = createMountPoint(true);
        expect(mockRepository.findMountPointByUrl("/path")).andReturn(expected);
        replay(mockRepository);
        assertEquals(expected, impl.resolveMountPoint("/path"));
        verify(mockRepository);
    }

    @Test
    public void testUrlFor() throws Exception {
        Map<String, MountHandler> handlers = new HashMap<String, MountHandler>();
        handlers.put("dummy", new DummyMountHandler());
        expect(mockRegistry.getRegisteredMountHandlers()).andReturn(handlers);
        replay(mockRegistry);
        assertEquals("/dummy", impl.urlFor(DummyPage.class, new PageParameters()).toString());
        verify(mockRegistry);
    }

    @Test
    public void testCanHandlePageClass() throws Exception {
        Map<String, MountHandler> handlers = new HashMap<String, MountHandler>();
        handlers.put("dummy", new DummyMountHandler());
        expect(mockRegistry.getRegisteredMountHandlers()).andReturn(handlers);
        replay(mockRegistry);
        assertTrue(impl.canHandlePageClass(DummyPage.class, new PageParameters()));
        verify(mockRegistry);
    }

    @Test
    public void testExistsPath() throws Exception {
        MountPoint expected = createMountPoint(true);
        expect(mockRepository.findMountPointByUrl("/path")).andReturn(expected);
        replay(mockRepository);
        assertTrue(impl.existsPath("path/"));
        verify(mockRepository);
    }

    @Test
    public void testFindMountPoints() throws Exception {
        MountPoint expected1 = createMountPoint(true);
        List<MountPoint> expected = Arrays.asList(expected1);
        expect(mockRepository.findMountPoints("123", "article")).andReturn(expected);
        replay(mockRepository);
        assertEquals(expected, impl.findMountPoints("123", "article"));
        verify(mockRepository);

    }

    private static class DummyMountHandler implements MountHandler {
        @Override
        public IRequestHandler getRequestHandler(Request request, MountPoint mountPoint) {
            return new BookmarkablePageRequestHandler(new PageProvider(DummyPage.class));
        }

        @Override
        public boolean canHandlePageClass(Class<? extends IRequestablePage> pageClazz, PageParameters pageParameters) {
            return true;
        }

        @Override
        public Url urlFor(Class<? extends IRequestablePage> pageClazz, PageParameters params) {
            return Url.parse("/dummy");
        }

        @Override
        public String getHandlerKey() {
            return "dummy";
        }

    }

    private static class DummyRequest extends Request {
        @Override
        public Url getUrl() {
            return Url.parse("/path");
        }

        @Override
        public Url getClientUrl() {
            return null;
        }

        @Override
        public Locale getLocale() {
            return null;
        }

        @Override
        public Charset getCharset() {
            return null;
        }

        @Override
        public Object getContainerRequest() {
            return null;
        }
    }
}
