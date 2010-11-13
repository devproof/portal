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

import org.apache.commons.lang.StringUtils;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.right.repository.RightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("rightService")
public class RightServiceImpl implements RightService {
    private List<Right> allRights;
    private long dirtyTime = 0l;
    private RightRepository rightRepository;

    @Override
    public List<Right> getAllRights() {
        return Collections.unmodifiableList(allRights);
    }

    @Override
    public long getDirtyTime() {
        return dirtyTime;
    }

    @Override
    @Transactional(readOnly = true)
    public synchronized void refreshGlobalApplicationRights() {
        allRights = rightRepository.findAll();
        dirtyTime = System.currentTimeMillis();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Right> findAllOrderByDescription() {
        return rightRepository.findAllOrderByDescription();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Right> findRightsStartingWith(String prefix) {
        return rightRepository.findRightsStartingWith(prefix);
    }

    @Override
    public Right newRightEntity() {
        return new Right();
    }

    @Override
    public Right newRightEntity(String right) {
        return new Right(right);
    }

    @Override
    @Transactional
    public void delete(Right entity) {
        rightRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Right> findAll() {
        return rightRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Right findById(String id) {
        return rightRepository.findById(id);
    }

    @Override
    @Transactional
    public void save(Right entity) {
        rightRepository.save(entity);
    }

    // TODO unit test
    @Override
    @Transactional(readOnly = true)
    public List<Right> findWhitespaceSeparatedRights(String rights) {
        String[] rightsSplitted = StringUtils.split(rights, " ");
        List<Right> convertedRights = new ArrayList<Right>();
        for(String rightName : rightsSplitted) {
            Right right = findById(rightName);
            if(right != null) {
                convertedRights.add(right);
            }
        }
        return convertedRights;
    }
    // TODO unit test
    @Override
    public String convertRightsToWhitespaceSeparated(List<Right> rights) {
        StringBuilder buf = new StringBuilder();
        for(Right right : rights) {
            buf.append(right.getRight()).append(' ');
        }
        return buf.toString();
    }

    @Autowired
    public void setRightRepository(RightRepository rightRepository) {
        this.rightRepository = rightRepository;
    }
}
