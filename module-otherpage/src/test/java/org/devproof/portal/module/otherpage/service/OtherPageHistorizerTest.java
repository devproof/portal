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

import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.entity.OtherPageHistorized;
import org.devproof.portal.module.otherpage.repository.OtherPageHistorizedRepository;
import org.devproof.portal.module.otherpage.repository.OtherPageRepository;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;

/**
 * @author Carsten Hufe
 */
public class OtherPageHistorizerTest {
    private OtherPageHistorizer impl;
    private OtherPageRepository mockRepo;
    private RightService mockRightService;
    private OtherPageHistorizedRepository mockHistorizedRepo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mockRepo = createStrictMock(OtherPageRepository.class);
        mockRightService = createStrictMock(RightService.class);
        mockHistorizedRepo = createStrictMock(OtherPageHistorizedRepository.class);
        impl = new OtherPageHistorizer();
        impl.setOtherPageRepository(mockRepo);
        impl.setRightService(mockRightService);
        impl.setOtherPageHistorizedRepository(mockHistorizedRepo);
    }


    @Test
    public void testHistorize() throws Exception {
        expect(mockRightService.convertRightsToWhitespaceSeparated(EasyMock.<List<Right>>anyObject())).andReturn("right1 right2");
        expect(mockHistorizedRepo.findLastVersionNumber(anyObject(OtherPage.class))).andReturn(1);
        expect(mockHistorizedRepo.save(EasyMock.<OtherPageHistorized>anyObject())).andReturn(null);
        replay(mockRightService, mockHistorizedRepo);
        OtherPage otherPage = new OtherPage();
        otherPage.setId(1);
        otherPage.setContent("content");
        impl.historize(otherPage, Action.CREATED);
        verify(mockRightService, mockHistorizedRepo);
    }

    @Test
    public void testRestore() throws Exception {
        expect(mockRightService.findWhitespaceSeparatedRights(anyObject(String.class))).andReturn(new ArrayList<Right>());
        // historize again
        expect(mockRightService.convertRightsToWhitespaceSeparated(EasyMock.<List<Right>>anyObject())).andReturn("right1 right2");
        expect(mockHistorizedRepo.findLastVersionNumber(anyObject(OtherPage.class))).andReturn(1);
        expect(mockHistorizedRepo.save(EasyMock.<OtherPageHistorized>anyObject())).andReturn(null);
        // restore
        expect(mockRepo.save(anyObject(OtherPage.class))).andReturn(new OtherPage());
        replay(mockRightService, mockHistorizedRepo, mockRepo);
        OtherPageHistorized historized = new OtherPageHistorized();
        historized.setOtherPage(new OtherPage());
        OtherPage restoredOtherPage = impl.restore(historized);
        assertFalse(restoredOtherPage.isUpdateModificationData());
        verify(mockRightService, mockHistorizedRepo, mockRepo);
    }

    @Test
    public void testDeleteHistory() throws Exception {
        mockHistorizedRepo.deleteHistoryForOtherPage(anyObject(OtherPage.class));
        replay(mockHistorizedRepo);
        impl.deleteHistory(new OtherPage());
        verify(mockHistorizedRepo);
    }
}
