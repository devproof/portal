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
package org.devproof.portal.core.module.configuration.dao;

import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.core.module.configuration.entity.ConfigurationEntity;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public interface ConfigurationDao extends GenericDao<ConfigurationEntity, String> {
	@Query("Select distinct(c) from ConfigurationEntity c")
	List<ConfigurationEntity> findAll();

	@Query("Select distinct(c.group) from ConfigurationEntity c where c.group not like 'hidden' order by c.group")
	List<String> findConfigurationGroups();

	@Query("Select distinct(c) from ConfigurationEntity c where c.group = ? and c.key not like 'hidden.%' order by c.description")
	List<ConfigurationEntity> findConfigurationsByGroup(String group);
}
