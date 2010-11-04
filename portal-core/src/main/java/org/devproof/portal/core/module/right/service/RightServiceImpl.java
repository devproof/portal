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

import org.devproof.portal.core.module.right.dao.RightRepository;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("rightService")
public class RightServiceImpl implements RightService {
    private List<RightEntity> allRights;
    private long dirtyTime = 0l;
    private RightRepository rightDao;

    @PostConstruct
    public void init() {
        refreshGlobalApplicationRights();
    }

    @Override
    public List<RightEntity> getAllRights() {
        return allRights;
    }

    @Override
    public long getDirtyTime() {
        return dirtyTime;
    }

    @Override
    public void refreshGlobalApplicationRights() {
        allRights = rightDao.findAll();
        dirtyTime = System.currentTimeMillis();
    }

    @Override
    public List<RightEntity> findAllOrderByDescription() {
        return rightDao.findAllOrderByDescription();
    }

    @Override
    public List<RightEntity> findRightsStartingWith(String prefix) {
        return rightDao.findRightsStartingWith(prefix);
    }

    @Override
    public RightEntity newRightEntity() {
        return new RightEntity();
    }

    @Override
    public RightEntity newRightEntity(String right) {
        return new RightEntity(right);
    }

    @Override
    public void delete(RightEntity entity) {
        rightDao.delete(entity);
    }

    @Override
    public List<RightEntity> findAll() {
        return rightDao.findAll();
    }

    @Override
    public RightEntity findById(String id) {
        return rightDao.findById(id);
    }

    @Override
    public void save(RightEntity entity) {
        rightDao.save(entity);
    }

    @Autowired
    public void setRightDao(RightRepository rightDao) {
        this.rightDao = rightDao;
    }
}
