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

import java.io.File;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.devproof.portal.core.module.configuration.entity.Configuration;
import org.devproof.portal.core.module.configuration.repository.ConfigurationRepository;
import org.devproof.portal.core.module.configuration.registry.ConfigurationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Carsten Hufe
 */
@Service("configurationService")
public class ConfigurationServiceImpl implements ConfigurationService {
	private ConfigurationRegistry configurationRegistry;
	private ConfigurationRepository configurationDao;
	private SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @PostConstruct
	public void init() {
		refreshGlobalConfiguration();
	}

	@Override
	public Object findAsObject(String key) {
		Configuration c = configurationRegistry.getConfiguration(key);
		if (c == null) {
			throw new NoSuchElementException("Configuration element \"" + key + "\" was not found!");
		}
		try {
			Class<?> clazz = Class.forName(c.getType());
			if (clazz.isEnum()) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				Enum<?> e = Enum.valueOf((Class) clazz, c.getValue());
				return e;
			} else {
				Constructor<?> constructor = clazz.getConstructor(String.class);
				return constructor.newInstance(c.getValue());
			}
		} catch (Exception e) {
			throw new NoSuchElementException("Configuration element \"" + key + "\" has an invalid type! " + e);
		}
	}

	@Override
	public Boolean findAsBoolean(String key) {
		return (Boolean) findAsObject(key);
	}

	@Override
	public Date findAsDate(String key) {
		Configuration c = configurationRegistry.getConfiguration(key);
		if (c == null) {
			throw new NoSuchElementException("Configuration element \"" + key + "\" was not found!");
		}
		if (Date.class.getName().equals(c.getType())) {
			try {
				return inputDateFormat.parse(c.getValue());
			} catch (ParseException e) {
				throw new NoSuchElementException("Configuration element \"" + key + "\" has not a valid date!");
			}
		} else {
			throw new NoSuchElementException("Configuration element \"" + key + "\" has not the type date!");
		}
	}

	@Override
	public Double findAsDouble(String key) {
		return (Double) findAsObject(key);
	}

	@Override
	public Enum<?> findAsEnum(String key) {
		return (Enum<?>) findAsObject(key);
	}

	@Override
	public Integer findAsInteger(String key) {
		return (Integer) findAsObject(key);
	}

	@Override
	public String findAsString(String key) {
		return (String) findAsObject(key);
	}

	@Override
	public File findAsFile(String key) {
		String path = findAsString(key);
		if (path.equals("java.io.tmpdir")) {
			path = System.getProperty("java.io.tmpdir");
		}
		return new File(path);
	}

	@Override
	public synchronized void refreshGlobalConfiguration() {
		List<Configuration> list = findAll();
		for (Configuration configuration : list) {
			configurationRegistry.registerConfiguration(configuration.getKey(), configuration);
		}
	}

	@Override
	public List<Configuration> findAll() {
		return configurationDao.findAll();
	}

	@Override
	public List<String> findConfigurationGroups() {
		return configurationDao.findConfigurationGroups();
	}

	@Override
	public List<Configuration> findConfigurationsByGroup(String group) {
		return configurationDao.findConfigurationsByGroup(group);
	}

	@Override
	public void delete(Configuration entity) {
		configurationDao.delete(entity);
	}

	@Override
	public Configuration findById(String id) {
		return configurationDao.findById(id);
	}

	@Override
	public void save(Configuration entity) {
		configurationDao.save(entity);
	}

	@Autowired
	public void setConfigurationDao(ConfigurationRepository configurationDao) {
		this.configurationDao = configurationDao;
	}

	@Autowired
	public void setConfigurationRegistry(ConfigurationRegistry configurationRegistry) {
		this.configurationRegistry = configurationRegistry;
	}
}
