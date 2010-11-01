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

import org.devproof.portal.module.otherpage.dao.OtherPageDao;
import org.devproof.portal.module.otherpage.entity.OtherPageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

/**
 * @author Carsten Hufe
 */
@Service("otherPageService")
public class OtherPageServiceImpl implements OtherPageService {
    private OtherPageDao otherPageDao;

    @Override
    public boolean existsContentId(String contentId) {
        return otherPageDao.existsContentId(contentId) > 0;
    }

    @Override
    public OtherPageEntity findOtherPageByContentId(String contentId) {
        return otherPageDao.findOtherPageByContentId(contentId);
    }

    @Override
    public OtherPageEntity newOtherPageEntity() {
        OtherPageEntity otherPage = new OtherPageEntity();
        otherPage.setAllRights(otherPageDao.findLastSelectedRights());
        return otherPage;
    }

    @Override
    public void delete(OtherPageEntity entity) {
        otherPageDao.delete(entity);
    }

    @Override
    public OtherPageEntity findById(Integer id) {
        return otherPageDao.findById(id);
    }

    @Override
    public void save(OtherPageEntity entity) {
        otherPageDao.save(entity);
    }

    @Autowired
    public void setOtherPageDao(OtherPageDao otherPageDao) {
        this.otherPageDao = otherPageDao;
    }
}
