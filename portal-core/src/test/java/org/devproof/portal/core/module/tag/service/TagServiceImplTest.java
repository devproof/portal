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
package org.devproof.portal.core.module.tag.service;

import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.tag.entity.AbstractTag;
import org.devproof.portal.core.module.tag.repository.TagRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Carsten Hufe
 */
public class TagServiceImplTest {
    private AbstractTagServiceImpl<DummyTag> impl;
    private TagRepository<DummyTag> mock;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mock = createStrictMock(TagRepository.class);
        impl = new AbstractTagServiceImpl<DummyTag>() {
            @Override
            public String getRelatedTagRight() {
                return "testright";
            }
        };
        impl.setTagDao(mock);
    }

    @Test
    public void testSave() {
        DummyTag e = new DummyTag();
        e.setTagname("tag");
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        DummyTag e = new DummyTag();
        e.setTagname("tag");
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindById() {
        DummyTag e = new DummyTag();
        e.setTagname("tag");
        expect(mock.findById("tag")).andReturn(e);
        replay(mock);
        assertEquals(impl.findById("tag"), e);
        verify(mock);
    }

    @Test
    public void testNewTagEntity() {
        expect(mock.getType()).andReturn(DummyTag.class);
        replay(mock);
        assertNotNull(impl.newTagEntity("tag"));
    }

    @Test
    public void testFindMostPopularTags1() {
        List<DummyTag> list = new ArrayList<DummyTag>();
        list.add(new DummyTag());
        list.add(new DummyTag());
        expect(mock.findMostPopularTags(0, 2)).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findMostPopularTags(0, 2));
        verify(mock);
    }

    @Test
    public void testFindMostPopularTags2() {
        List<DummyTag> list = new ArrayList<DummyTag>();
        list.add(new DummyTag());
        list.add(new DummyTag());
        Role role = new Role();
        expect(mock.findMostPopularTags(role, "testright", 0, 2)).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findMostPopularTags(role, 0, 2));
        verify(mock);
    }

    @Test
    public void testFindTagsStartingWith() {
        List<DummyTag> list = new ArrayList<DummyTag>();
        list.add(new DummyTag());
        list.add(new DummyTag());
        expect(mock.findTagsStartingWith("prefix")).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findTagsStartingWith("prefix"));
        verify(mock);
    }

    @Test
    public void testDeleteUnusedTags() {
        mock.deleteUnusedTags();
        replay(mock);
        impl.deleteUnusedTags();
        verify(mock);
    }

    @Test
    public void testFindByIdAndCreateIfNotExists() {
        expect(mock.findById("sampletag")).andReturn(null);
        expect(mock.getType()).andReturn(DummyTag.class);
        expect(mock.save((DummyTag) anyObject())).andReturn(null);
        replay(mock);
        DummyTag newTag = impl.findByIdAndCreateIfNotExists("sampletag");
        assertEquals("sampletag", newTag.getTagname());
        verify(mock);
    }

    private static class DummyTag extends AbstractTag<Object> {

        private static final long serialVersionUID = 1L;

        public DummyTag() {
            super();
            setTagname(String.valueOf(Math.random()));
        }

        @Override
        public List<?> getReferencedObjects() {
            return null;
        }

        @Override
        public void setReferencedObjects(List<Object> refObjs) {

        }

    }
}
