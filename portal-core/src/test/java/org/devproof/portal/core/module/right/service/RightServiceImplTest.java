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
package org.devproof.portal.core.module.right.service;

import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.repository.RightRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author Carsten Hufe
 */
public class RightServiceImplTest {
    private RightServiceImpl impl;
    private RightRepository mock;

    @Before
    public void setUp() throws Exception {
        mock = createStrictMock(RightRepository.class);
        impl = new RightServiceImpl();
        impl.setRightRepository(mock);
    }

    @Test
    public void testSave() {
        Right e = impl.newRightEntity();
        e.setRight("right");
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        Right e = impl.newRightEntity();
        e.setRight("right");
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindAll() {
        List<Right> list = new ArrayList<Right>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAll());
        verify(mock);
    }

    @Test
    public void testFindById() {
        Right e = impl.newRightEntity();
        e.setRight("right");
        expect(mock.findById("right")).andReturn(e);
        replay(mock);
        assertEquals(impl.findById("right"), e);
        verify(mock);
    }

    @Test
    public void testNewRightEntity() {
        assertNotNull(impl.newRightEntity());
    }

    @Test
    public void testNewRightEntityParam() {
        Right r = impl.newRightEntity("hello");
        assertNotNull(r);
        assertNotNull(r.getRight());
    }

    @Test
    public void testGetDirtyTime() {
        impl.refreshGlobalApplicationRights();
        assertTrue(impl.getDirtyTime() > 0);
    }

    @Test
    public void testFindAllOrderByDescription() {
        List<Right> list = new ArrayList<Right>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findAllOrderByDescription()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAllOrderByDescription());
        verify(mock);
    }

    @Test
    public void testFindRightsStartingWith() {
        List<Right> list = new ArrayList<Right>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findRightsStartingWith("prefix")).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findRightsStartingWith("prefix"));
        verify(mock);
    }

    @Test
    public void testGetAllRights() {
        List<Right> list = new ArrayList<Right>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        assertEquals(impl.getAllRights(), list);
        verify(mock);
    }
}
