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
package org.devproof.portal.core.module.right.service;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.right.entity.Right;

import java.util.List;

/**
 * Manage user rights
 *
 * @author Carsten Hufe
 */
public interface RightService extends CrudService<Right, String> {
    /**
     * Returns all rights
     */
    List<Right> findAll();

    /**
     * Returns the dirty time
     *
     * @return last refreshed unix time
     */
    long getDirtyTime();

    /**
     * Refreshes the global instanz of all rights
     */
    void refreshGlobalApplicationRights();

    /**
     * Returns all rights from member variable
     *
     * @return list with all rights
     */
    List<Right> getAllRights();

    /**
     * Returns all rights starting with the given prefix
     *
     * @param prefix right prefix
     * @return list with matching rights
     */
    List<Right> findRightsStartingWith(String prefix);

    /**
     * Returns all rights ordered by description
     *
     * @return list with rights
     */
    List<Right> findAllOrderByDescription();

    /**
     * Returns a new instance of Right
     *
     * @return new instance of Right
     */
    Right newRightEntity();

    /**
     * Returns a new instance of Right
     *
     * @param right right key
     * @return new instance of Right
     */
	Right newRightEntity(String right);

    /**
     * Returns a list with tags, expects a string with whitespace separated tags
     */
    List<Right> findWhitespaceSeparatedRights(String rights);

    /**
     * Returns a whitespace separated list of rights
     */
    String convertRightsToWhitespaceSeparated(List<Right> rights);
}
