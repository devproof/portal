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

import org.devproof.portal.core.module.box.entity.Box;
import org.devproof.portal.core.module.box.repository.BoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("boxService")
public class BoxServiceImpl implements BoxService {
	private BoxRepository boxRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Box> findAllOrderedBySort() {
        return boxRepository.findAllOrderedBySort();
    }

    @Override
    @Transactional(readOnly = true)
	public Box findBoxBySort(Integer sort) {
		return boxRepository.findBoxBySort(sort);
	}

	@Override
    @Transactional(readOnly = true)
	public Integer getMaxSortNum() {
		Integer sort = boxRepository.getMaxSortNum();
		if (sort == null) {
			return 1;
		}
		return sort + 1;
	}

	@Override
	public Box newBoxEntity() {
		return new Box();
	}

    @Override
    @Transactional
	public void delete(Box entity) {
		int maxSort = boxRepository.getMaxSortNum();
		int deleteSort = entity.getSort();
		boxRepository.delete(entity);
		if (maxSort > deleteSort) {
			for (int i = deleteSort + 1; i <= maxSort; i++) {
				Box box = boxRepository.findBoxBySort(i);
				box.setSort(box.getSort() - 1);
				boxRepository.save(box);
			}
		}

	}

	@Override
    @Transactional(readOnly = true)
	public Box findById(Integer id) {
		return boxRepository.findById(id);
	}

	@Override
    @Transactional
	public void save(Box entity) {
		boxRepository.save(entity);
	}

	@Override
    @Transactional
	public void moveDown(Box box) {
		int maxSort = boxRepository.getMaxSortNum();
		boolean isNotLowestBox = box.getSort() < maxSort;
		if (isNotLowestBox) {
			Box moveDown = box;
			Box moveUp = boxRepository.findBoxBySort(box.getSort() + 1);
			moveUp.setSort(moveUp.getSort() - 1);
			moveDown.setSort(moveDown.getSort() + 1);
			boxRepository.save(moveUp);
			boxRepository.save(moveDown);
		}
	}

	@Override
    @Transactional
	public void moveUp(Box box) {
		boolean isNotHighestBox = box.getSort() > 1;
		if (isNotHighestBox) {
			Box moveUp = box;
			Box moveDown = boxRepository.findBoxBySort(box.getSort() - 1);
			moveUp.setSort(moveUp.getSort() - 1);
			moveDown.setSort(moveDown.getSort() + 1);
			boxRepository.save(moveUp);
			boxRepository.save(moveDown);
		}
	}

    @Autowired
	public void setBoxRepository(BoxRepository boxRepository) {
		this.boxRepository = boxRepository;
	}
}
