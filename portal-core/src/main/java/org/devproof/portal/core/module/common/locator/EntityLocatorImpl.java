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
package org.devproof.portal.core.module.common.locator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.devproof.portal.core.config.ModuleConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Carsten Hufe
 */
public class EntityLocatorImpl implements ApplicationContextAware, EntityLocator {
	private ApplicationContext context;

	@Override
	public Collection<?> getEntities() {
		@SuppressWarnings("unchecked")
		Map<String, ModuleConfiguration> beans = context.getBeansOfType(ModuleConfiguration.class);
		List<Object> back = new ArrayList<Object>();
		for (ModuleConfiguration module : beans.values()) {
			back.addAll(module.getEntities());
		}
		return back;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}

}
