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
import org.devproof.portal.core.module.common.service.CrudService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public interface BoxService extends CrudService<Box, Integer> {
    /**
     * Returns a new instance of Box
     *
     * @return new instance of {@link org.devproof.portal.core.module.box.entity.Box}
     */
    Box newBoxEntity();

    /**
     * Returns the highest sort number
     *
     * @return highest sort number
     */
    Integer getMaxSortNum();

    /**
     * Returns the box with the given sort number
     *
     * @param sort order number
     */
    Box findBoxBySort(Integer sort);

    /**
     * Returns all boxes ordered by sort number
     */
    List<Box> findAllOrderedBySort();

    /**
     * Moves the box down
     *
     * @param box box to move
     */
    void moveDown(Box box);

    /**
     * Moves the box up
     *
     * @param box box to move
     */
	void moveUp(Box box);
}
