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
package org.devproof.portal.core.module.historization.service;

/**
 * Converter to a history object and back
 *
 * @author Carsten Hufe
 */
public interface Historizer<ENTITY, HISTORIZED> {
    /**
     * Creates the history object
     * @param entity entity to historize
     * @return history object
     */
    void historize(ENTITY entity, Action action);

    /**
     * Restores the entity, copies the values from historized to entity
     * @param historized history object
     */
    ENTITY restore(HISTORIZED historized);

    /**
     * Cleans the history, when the entity is deleted
     */
    void deleteHistory(ENTITY entity);
}
