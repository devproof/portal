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

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.dao.TagRepository;
import org.devproof.portal.core.module.tag.entity.BaseTagEntity;
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
    private AbstractTagServiceImpl<DummyTagEntity> impl;
    private TagRepository<DummyTagEntity> mock;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mock = createStrictMock(TagRepository.class);
        impl = new AbstractTagServiceImpl<DummyTagEntity>() {
            @Override
            public String getRelatedTagRight() {
                return "testright";
            }
        };
        impl.setTagDao(mock);
    }

    @Test
    public void testSave() {
        DummyTagEntity e = new DummyTagEntity();
        e.setTagname("tag");
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        DummyTagEntity e = new DummyTagEntity();
        e.setTagname("tag");
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindById() {
        DummyTagEntity e = new DummyTagEntity();
        e.setTagname("tag");
        expect(mock.findById("tag")).andReturn(e);
        replay(mock);
        assertEquals(impl.findById("tag"), e);
        verify(mock);
    }

    @Test
    public void testNewTagEntity() {
        expect(mock.getType()).andReturn(DummyTagEntity.class);
        replay(mock);
        assertNotNull(impl.newTagEntity("tag"));
    }

    @Test
    public void testFindMostPopularTags1() {
        List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
        list.add(new DummyTagEntity());
        list.add(new DummyTagEntity());
        expect(mock.findMostPopularTags(0, 2)).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findMostPopularTags(0, 2));
        verify(mock);
    }

    @Test
    public void testFindMostPopularTags2() {
        List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
        list.add(new DummyTagEntity());
        list.add(new DummyTagEntity());
        RoleEntity role = new RoleEntity();
        expect(mock.findMostPopularTags(role, "testright", 0, 2)).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findMostPopularTags(role, 0, 2));
        verify(mock);
    }

    @Test
    public void testFindTagsStartingWith() {
        List<DummyTagEntity> list = new ArrayList<DummyTagEntity>();
        list.add(new DummyTagEntity());
        list.add(new DummyTagEntity());
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
        expect(mock.getType()).andReturn(DummyTagEntity.class);
        expect(mock.save((DummyTagEntity) anyObject())).andReturn(null);
        replay(mock);
        DummyTagEntity newTag = impl.findByIdAndCreateIfNotExists("sampletag");
        assertEquals("sampletag", newTag.getTagname());
        verify(mock);
    }

    private static class DummyTagEntity extends BaseTagEntity<Object> {

        private static final long serialVersionUID = 1L;

        public DummyTagEntity() {
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
