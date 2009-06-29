/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.configuration.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.devproof.portal.core.module.common.service.CrudService;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;

/**
 * @author Carsten Hufe Methods to load the configuration values
 */
public interface ConfigurationService extends CrudService<ConfigurationEntity, String> {
	/**
	 * Returns a object (for everything else) from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as object
	 */
	public Object findAsObject(String key);

	/**
	 * Returns a date from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as boolean
	 */
	public Boolean findAsBoolean(String key);

	/**
	 * Returns a date from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as date
	 */
	public Date findAsDate(String key);

	/**
	 * Returns a integer from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as integer
	 */
	public Integer findAsInteger(String key);

	/**
	 * Returns a string from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as string
	 */
	public String findAsString(String key);

	/**
	 * Returns a File from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as file
	 */
	public File findAsFile(String key);

	/**
	 * Returns a double from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as double
	 */
	public Double findAsDouble(String key);

	/**
	 * Returns a enum from the configuration table
	 * 
	 * @param key
	 *            configuration key
	 * @return configuration entry as enum
	 */
	public Enum<?> findAsEnum(String key);

	/**
	 * Returns all configurations
	 * 
	 * @return all entries as list
	 */
	public List<ConfigurationEntity> findAll();

	/**
	 * Refresh the global configuration map / cache
	 */
	public void refreshGlobalConfiguration();

	/**
	 * Returns all configuration groups
	 * 
	 * @return configuration groups in a list
	 */
	public List<String> findConfigurationGroups();

	/**
	 * Finds all {@link ConfigurationEntity} by group
	 * 
	 * @param group
	 *            configuration group
	 * @return configuration entries filtered by group
	 */
	public List<ConfigurationEntity> findConfigurationsByGroup(String group);

}
