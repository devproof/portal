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
package org.devproof.portal.core.module.box.service;

import org.devproof.portal.core.module.box.dao.BoxDao;
import org.devproof.portal.core.module.box.entity.BoxEntity;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class BoxServiceImpl implements BoxService {
    private BoxDao boxDao;

    @Override
    public List<BoxEntity> findAllOrderedBySort() {
        return boxDao.findAllOrderedBySort();
    }

    @Override
    public BoxEntity findBoxBySort(Integer sort) {
        return boxDao.findBoxBySort(sort);
    }

    @Override
    public Integer getMaxSortNum() {
        Integer sort = boxDao.getMaxSortNum();
        if (sort == null) {
            return 1;
        }
        return sort + 1;
    }

    @Override
    public BoxEntity newBoxEntity() {
        return new BoxEntity();
    }

    @Override
    public void delete(BoxEntity entity) {
        int maxSort = boxDao.getMaxSortNum();
        int deleteSort = entity.getSort();
        boxDao.delete(entity);
        if (maxSort > deleteSort) {
            for (int i = deleteSort + 1; i <= maxSort; i++) {
                BoxEntity box = boxDao.findBoxBySort(i);
                box.setSort(box.getSort() - 1);
                boxDao.save(box);
            }
        }

    }

    @Override
    public BoxEntity findById(Integer id) {
        return boxDao.findById(id);
    }

    @Override
    public void save(BoxEntity entity) {
        boxDao.save(entity);
    }

    @Override
    public void moveDown(BoxEntity box) {
        int maxSort = boxDao.getMaxSortNum();
        boolean isNotLowestBox = box.getSort() < maxSort;
        if (isNotLowestBox) {
            BoxEntity moveDown = box;
            BoxEntity moveUp = boxDao.findBoxBySort(box.getSort() + 1);
            moveUp.setSort(moveUp.getSort() - 1);
            moveDown.setSort(moveDown.getSort() + 1);
            boxDao.save(moveUp);
            boxDao.save(moveDown);
        }
    }

    @Override
    public void moveUp(BoxEntity box) {
        boolean isNotHighestBox = box.getSort() > 1;
        if (isNotHighestBox) {
            BoxEntity moveUp = box;
            BoxEntity moveDown = boxDao.findBoxBySort(box.getSort() - 1);
            moveUp.setSort(moveUp.getSort() - 1);
            moveDown.setSort(moveDown.getSort() + 1);
            boxDao.save(moveUp);
            boxDao.save(moveDown);
        }
    }

    @Required
    public void setBoxDao(BoxDao boxDao) {
        this.boxDao = boxDao;
    }
}
