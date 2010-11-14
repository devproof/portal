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
package org.devproof.portal.core.module.configuration.service;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.configuration.entity.Configuration;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * @author Carsten Hufe Methods to load the configuration values
 */
public interface ConfigurationService extends CrudService<Configuration, String> {
    /**
     * Returns a object (for everything else) from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as object
     */
    Object findAsObject(String key);

    /**
     * Returns a date from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as boolean
     */
    Boolean findAsBoolean(String key);

    /**
     * Returns a date from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as date
     */
    Date findAsDate(String key);

    /**
     * Returns a integer from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as integer
     */
    Integer findAsInteger(String key);

    /**
     * Returns a string from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as string
     */
    String findAsString(String key);

    /**
     * Returns a File from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as file
     */
    File findAsFile(String key);

    /**
     * Returns a double from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as double
     */
    Double findAsDouble(String key);

    /**
     * Returns a enum from the configuration table
     *
     * @param key configuration key
     * @return configuration entry as enum
     */
    Enum<?> findAsEnum(String key);

    /**
     * Returns all configurations
     *
     * @return all entries as list
     */
    List<Configuration> findAll();

    /**
     * Returns all configuration groups
     *
     * @return configuration groups in a list
     */
    List<String> findConfigurationGroups();

    /**
     * Finds all {@link org.devproof.portal.core.module.configuration.entity.Configuration} by group
     *
     * @param group configuration group
     * @return configuration entries filtered by group
     */
	List<Configuration> findConfigurationsByGroup(String group);

    /**
     * Refreshes the cached configuration
     */
    void refreshGlobalApplicationConfiguration();
}
