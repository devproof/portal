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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Carsten Hufe
 */
@Service("otherPageService")
public class OtherPageServiceImpl implements OtherPageService {
    private OtherPageRepository otherPageRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsContentId(String contentId) {
        return otherPageRepository.existsContentId(contentId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public OtherPage findOtherPageByContentId(String contentId) {
        return otherPageRepository.findOtherPageByContentId(contentId);
    }

    @Override
    @Transactional(readOnly = true)
    public OtherPage newOtherPageEntity() {
        OtherPage otherPage = new OtherPage();
        otherPage.setAllRights(otherPageRepository.findLastSelectedRights());
        return otherPage;
    }

    @Override
    @Transactional
    public void delete(OtherPage entity) {
        otherPageRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public OtherPage findById(Integer id) {
        return otherPageRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(OtherPage entity) {
        otherPageRepository.save(entity);
    }

    @Autowired
    public void setOtherPageRepository(OtherPageRepository otherPageRepository) {
        this.otherPageRepository = otherPageRepository;
    }
}
