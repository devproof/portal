/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.devproof.portal.core.module.configuration.dao.ConfigurationDao;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;
import org.devproof.portal.core.module.configuration.registry.ConfigurationRegistry;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class ConfigurationServiceImpl implements ConfigurationService {
	private ConfigurationRegistry configurationRegistry;
	private ConfigurationDao configurationDao;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat dateTimeFormat;

	public void init() {
		refreshGlobalConfiguration();
	}

	@Override
	public Object findAsObject(final String key) {
		final ConfigurationEntity c = configurationRegistry.getConfiguration(key);
		if (c == null) {
			throw new NoSuchElementException("Configuration element \"" + key + "\" was not found!");
		}
		try {
			final Class<?> clazz = Class.forName(c.getType());
			if (clazz.isEnum()) {
				@SuppressWarnings("unchecked")
				final Enum<?> e = Enum.valueOf((Class) clazz, c.getValue());
				return e;
			} else {
				final Constructor<?> constructor = clazz.getConstructor(String.class);
				return constructor.newInstance(c.getValue());
			}
		} catch (final Exception e) {
			throw new NoSuchElementException("Configuration element \"" + key + "\" has an invalid type! " + e);
		}
	}

	@Override
	public Boolean findAsBoolean(final String key) {
		return (Boolean) findAsObject(key);
	}

	@Override
	public Date findAsDate(final String key) {
		final ConfigurationEntity c = configurationRegistry.getConfiguration(key);
		if (c == null) {
			throw new NoSuchElementException("Configuration element \"" + key + "\" was not found!");
		}
		if (Date.class.getName().equals(c.getType())) {
			try {
				return dateFormat.parse(c.getValue());
			} catch (final ParseException e) {
				throw new NoSuchElementException("Configuration element \"" + key + "\" has not a valid date!");
			}
		} else {
			throw new NoSuchElementException("Configuration element \"" + key + "\" has not the type date!");
		}
	}

	@Override
	public Double findAsDouble(final String key) {
		return (Double) findAsObject(key);
	}

	@Override
	public Enum<?> findAsEnum(final String key) {
		return (Enum<?>) findAsObject(key);
	}

	@Override
	public Integer findAsInteger(final String key) {
		return (Integer) findAsObject(key);
	}

	@Override
	public String findAsString(final String key) {
		return (String) findAsObject(key);
	}

	@Override
	public File findAsFile(final String key) {
		String path = findAsString(key);
		if (path.equals("java.io.tmpdir")) {
			path = System.getProperty("java.io.tmpdir");
		}
		return new File(path);
	}

	@Override
	public void refreshGlobalConfiguration() {
		final List<ConfigurationEntity> list = findAll();
		for (final ConfigurationEntity configuration : list) {
			configurationRegistry.registerConfiguration(configuration.getKey(), configuration);
		}
		// Refresh global date formater
		dateFormat.applyPattern(findAsString("date_format"));
		dateTimeFormat.applyPattern(findAsString("date_time_format"));
	}

	@Override
	public List<ConfigurationEntity> findAll() {
		return configurationDao.findAll();
	}

	@Override
	public List<String> findConfigurationGroups() {
		return configurationDao.findConfigurationGroups();
	}

	@Override
	public List<ConfigurationEntity> findConfigurationsByGroup(final String group) {
		return configurationDao.findConfigurationsByGroup(group);
	}

	@Override
	public void delete(final ConfigurationEntity entity) {
		configurationDao.delete(entity);
	}

	@Override
	public ConfigurationEntity findById(final String id) {
		return configurationDao.findById(id);
	}

	@Override
	public void save(final ConfigurationEntity entity) {
		configurationDao.save(entity);
	}

	@Required
	public void setConfigurationDao(final ConfigurationDao configurationDao) {
		this.configurationDao = configurationDao;
	}

	@Required
	public void setDateFormat(final SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Required
	public void setDateTimeFormat(final SimpleDateFormat dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	@Required
	public void setConfigurationRegistry(final ConfigurationRegistry configurationRegistry) {
		this.configurationRegistry = configurationRegistry;
	}
}
