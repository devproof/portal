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

import junit.framework.TestCase;
import org.devproof.portal.core.module.right.dao.RightDao;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Carsten Hufe
 */
public class RightServiceImplTest {
    private RightServiceImpl impl;
    private RightDao mock;

    @Before
    public void setUp() throws Exception {
        mock = createStrictMock(RightDao.class);
        impl = new RightServiceImpl();
        impl.setRightDao(mock);
    }

    @Test
    public void testSave() {
        RightEntity e = impl.newRightEntity();
        e.setRight("right");
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        RightEntity e = impl.newRightEntity();
        e.setRight("right");
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindAll() {
        List<RightEntity> list = new ArrayList<RightEntity>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAll());
        verify(mock);
    }

    @Test
    public void testFindById() {
        RightEntity e = impl.newRightEntity();
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
        RightEntity r = impl.newRightEntity("hello");
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
        List<RightEntity> list = new ArrayList<RightEntity>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findAllOrderByDescription()).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findAllOrderByDescription());
        verify(mock);
    }

    @Test
    public void testFindRightsStartingWith() {
        List<RightEntity> list = new ArrayList<RightEntity>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findRightsStartingWith("prefix")).andReturn(list);
        replay(mock);
        assertEquals(list, impl.findRightsStartingWith("prefix"));
        verify(mock);
    }

    @Test
    public void testGetAllRights() {
        List<RightEntity> list = new ArrayList<RightEntity>();
        list.add(impl.newRightEntity());
        list.add(impl.newRightEntity());
        expect(mock.findAll()).andReturn(list);
        replay(mock);
        impl.init();
        assertEquals(impl.getAllRights(), list);
        verify(mock);
    }
}
