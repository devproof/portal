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
package org.devproof.portal.module.otherpage.service;

import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.repository.OtherPageRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Checks the delegating functionality of the OtherPageServiceImpl
 *
 * @author Carsten Hufe
 */
public class OtherPageServiceImplTest {
    private OtherPageServiceImpl impl;
    private OtherPageRepository mock;

    @Before
    public void setUp() throws Exception {
        mock = createStrictMock(OtherPageRepository.class);
        impl = new OtherPageServiceImpl();
        impl.setOtherPageDao(mock);
    }

    @Test
    public void testSave() {
        OtherPage e = createOtherPageEntity();
        expect(mock.save(e)).andReturn(e);
        replay(mock);
        impl.save(e);
        verify(mock);
    }

    @Test
    public void testDelete() {
        OtherPage e = createOtherPageEntity();
        e.setId(1);
        mock.delete(e);
        replay(mock);
        impl.delete(e);
        verify(mock);
    }

    @Test
    public void testFindById() {
        OtherPage e = createOtherPageEntity();
        expect(mock.findById(1)).andReturn(e);
        replay(mock);
        assertEquals(impl.findById(1), e);
        verify(mock);
    }

    @Test
    public void testNewOtherPageEntity() {
        Assert.assertNotNull(impl.newOtherPageEntity());
    }

    @Test
    public void testExistsContentId() {
        expect(mock.existsContentId("contentId")).andReturn(1l);
        replay(mock);
        assertTrue(impl.existsContentId("contentId"));
        verify(mock);
    }

    private OtherPage createOtherPageEntity() {
        OtherPage e = new OtherPage();
        e.setId(1);
        return e;
    }
}
