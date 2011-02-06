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
package org.devproof.portal.core.module.box.service;

import org.devproof.portal.core.module.box.entity.Box;
import org.devproof.portal.core.module.box.repository.BoxRepository;
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
public class BoxServiceImplTest {
    private BoxServiceImpl impl;
    private BoxRepository mock;

    @Before
    public void setUp() throws Exception {
        mock = createStrictMock(BoxRepository.class);
        impl = new BoxServiceImpl();
        impl.setBoxRepository(mock);
    }

    @Test
    public void testSave() {
        Box e = impl.newBoxEntity();
        e.setId(1);
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        Box e = impl.newBoxEntity();
        e.setId(1);
        e.setSort(1);
        expect(mock.getMaxSortNum()).andReturn(1);
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindById() {
        Box e = impl.newBoxEntity();
        e.setId(1);
        expect(mock.findById(1)).andReturn(e);
        replay(mock);
        assertEquals(impl.findById(1), e);
        verify(mock);
    }

    @Test
    public void testNewBoxEntity() {
        assertNotNull(impl.newBoxEntity());
    }

    @Test
    public void testFindAllOrderedBySort() {
        List<Box> list = new ArrayList<Box>();
        list.add(impl.newBoxEntity());
        list.add(impl.newBoxEntity());
        expect(mock.findAllOrderedBySort()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAllOrderedBySort());
        verify(mock);
    }

    @Test
    public void testFindBoxBySort() {
        Box e = impl.newBoxEntity();
        e.setId(1);
        expect(mock.findBoxBySort(5)).andReturn(e);
        replay(mock);
        assertEquals(e, impl.findBoxBySort(5));
        verify(mock);
    }

    @Test
    public void testGetMaxSortNum() {
        expect(mock.getMaxSortNum()).andReturn(5);
        replay(mock);
        assertEquals(Integer.valueOf(6), impl.getMaxSortNum());
        verify(mock);
    }

    @Test
    public void testMoveDown() {
        Box e1 = impl.newBoxEntity();
        e1.setId(1);
        e1.setSort(1);
        Box e2 = impl.newBoxEntity();
        e2.setId(2);
        e2.setSort(2);
        expect(mock.getMaxSortNum()).andReturn(2);
        expect(mock.findBoxBySort(2)).andReturn(e2);
        expect(mock.save(e2)).andReturn(e2);
        expect(mock.save(e1)).andReturn(e1);
        replay(mock);
        impl.moveDown(e1);
        assertEquals(Integer.valueOf(2), e1.getSort());
        assertEquals(Integer.valueOf(1), e2.getSort());
        verify(mock);
    }

    @Test
    public void testMoveUp() {
        Box e1 = impl.newBoxEntity();
        e1.setId(1);
        e1.setSort(1);
        Box e2 = impl.newBoxEntity();
        e2.setId(2);
        e2.setSort(2);
        expect(mock.findBoxBySort(1)).andReturn(e1);
        expect(mock.save(e2)).andReturn(e2);
        expect(mock.save(e1)).andReturn(e1);
        replay(mock);
        impl.moveUp(e2);
        assertEquals(Integer.valueOf(2), e1.getSort());
        assertEquals(Integer.valueOf(1), e2.getSort());
        verify(mock);
    }
}
