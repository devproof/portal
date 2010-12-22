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
import org.devproof.portal.core.module.historization.service.Historizer;
import org.devproof.portal.core.module.right.service.RightService;
import org.devproof.portal.module.otherpage.entity.OtherPage;
import org.devproof.portal.module.otherpage.entity.OtherPageHistorized;
import org.devproof.portal.module.otherpage.repository.OtherPageHistorizedRepository;
import org.devproof.portal.module.otherpage.repository.OtherPageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Historizer for OtherPages
 *
 * @author Carsten Hufe
 */
@Component
public class OtherPageHistorizer implements Historizer<OtherPage, OtherPageHistorized> {
    private RightService rightService;
    private OtherPageHistorizedRepository otherPageHistorizedRepository;
    private OtherPageRepository otherPageRepository;

    @Override
    public void historize(OtherPage otherPage, Action action) {
        historize(otherPage, action, null);
    }

    private void historize(OtherPage otherPage, Action action, Integer restoredVersion) {
        OtherPageHistorized historized = new OtherPageHistorized();
        historized.copyFrom(otherPage);
        historized.setRights(rightService.convertRightsToWhitespaceSeparated(otherPage.getAllRights()));
        historized.setOtherPage(otherPage);
        historized.setAction(action);
        historized.setActionAt(new Date());
        historized.setVersionNumber(retrieveNextVersionNumber(otherPage));
        historized.setRestoredFromVersion(restoredVersion);
        otherPageHistorizedRepository.save(historized);
    }

    private Integer retrieveNextVersionNumber(OtherPage otherPage) {
        Integer nextNumber = otherPageHistorizedRepository.findLastVersionNumber(otherPage);
        if(nextNumber == null) {
            nextNumber = 0;
        }
        return nextNumber + 1;
    }

    @Override
    public OtherPage restore(OtherPageHistorized historized) {
        OtherPage otherPage = historized.getOtherPage();
        otherPage.copyFrom(historized);
        otherPage.setAllRights(rightService.findWhitespaceSeparatedRights(historized.getRights()));
        otherPage.setUpdateModificationData(false);
        historize(otherPage, Action.RESTORED, historized.getVersionNumber());
        otherPageRepository.save(otherPage);
        return otherPage;
    }

    @Override
    public void deleteHistory(OtherPage otherPage) {
        otherPageHistorizedRepository.deleteHistoryForOtherPage(otherPage);
    }

    @Autowired
    public void setRightService(RightService rightService) {
        this.rightService = rightService;
    }

    @Autowired
    public void setOtherPageHistorizedRepository(OtherPageHistorizedRepository otherPageHistorizedRepository) {
        this.otherPageHistorizedRepository = otherPageHistorizedRepository;
    }

    @Autowired
    public void setOtherPageRepository(OtherPageRepository otherPageRepository) {
        this.otherPageRepository = otherPageRepository;
    }
}
