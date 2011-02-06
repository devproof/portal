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
package org.devproof.portal.module.otherpage.service;

import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.entity.OtherPageHistorized;
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
    private OtherPageRepository mockRepository;
    private OtherPageHistorizer mockHistorizer;

    @Before
    public void setUp() throws Exception {
        mockRepository = createStrictMock(OtherPageRepository.class);
        mockHistorizer = createStrictMock(OtherPageHistorizer.class);
        impl = new OtherPageServiceImpl();
        impl.setOtherPageRepository(mockRepository);
        impl.setOtherPageHistorizer(mockHistorizer);
    }


    @Test
    public void testRestoreFromHistory() throws Exception {
        OtherPageHistorized historized = new OtherPageHistorized();
        expect(mockHistorizer.restore(historized)).andReturn(new OtherPage());
        replay(mockHistorizer);
        impl.restoreFromHistory(historized);
        verify(mockHistorizer);
    }

    @Test
    public void testSave() {
        OtherPage e = createOtherPageEntity();
        expect(mockRepository.save(e)).andReturn(e);
        mockHistorizer.historize(e, Action.MODIFIED);
        replay(mockRepository, mockHistorizer);
        impl.save(e);
        verify(mockRepository, mockHistorizer);
    }

    @Test
    public void testDelete() {
        OtherPage e = createOtherPageEntity();
        e.setId(1);
        mockRepository.delete(e);
        mockHistorizer.deleteHistory(e);
        replay(mockRepository, mockHistorizer);
        impl.delete(e);
        verify(mockRepository, mockHistorizer);
    }

    @Test
    public void testFindById() {
        OtherPage e = createOtherPageEntity();
        expect(mockRepository.findById(1)).andReturn(e);
        replay(mockRepository);
        assertEquals(impl.findById(1), e);
        verify(mockRepository);
    }

    @Test
    public void testNewOtherPageEntity() {
        Assert.assertNotNull(impl.newOtherPageEntity());
    }

    @Test
    public void testExistsContentId() {
        expect(mockRepository.existsContentId("contentId")).andReturn(1l);
        replay(mockRepository);
        assertTrue(impl.existsContentId("contentId"));
        verify(mockRepository);
    }

    private OtherPage createOtherPageEntity() {
        OtherPage e = new OtherPage();
        e.setId(1);
        return e;
    }
}
