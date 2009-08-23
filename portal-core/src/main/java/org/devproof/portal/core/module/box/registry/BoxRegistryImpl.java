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
package org.devproof.portal.core.module.box.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.module.box.locator.BoxLocator;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Carsten Hufe
 */
public class BoxRegistryImpl implements BoxRegistry, InitializingBean {
	private BoxLocator boxLocator;

	private final Map<String, BoxConfiguration> boxes = new HashMap<String, BoxConfiguration>();

	@Override
	public String getNameBySimpleClassName(final String clazz) {
		final BoxConfiguration box = boxes.get(clazz);
		if (box != null) {
			return box.getName();
		}
		return "[undefined]";
	}

	@Override
	public List<BoxConfiguration> getRegisteredBoxes() {
		return new ArrayList<BoxConfiguration>(boxes.values());
	}

	@Override
	public boolean isBoxClassRegistered(final String clazz) {
		return boxes.containsKey(clazz);
	}

	@Override
	public void registerBox(final BoxConfiguration box) {
		boxes.put(box.getBoxClass().getSimpleName(), box);
	};

	@Override
	public void removeBox(final BoxConfiguration box) {
		boxes.remove(box.getBoxClass().getSimpleName());
	}

	@Override
	public BoxConfiguration getBoxConfigurationBySimpleClassName(final String simpleClazz) {
		final BoxConfiguration box = boxes.get(simpleClazz);
		if (box != null) {
			final BoxConfiguration back = new BoxConfiguration();
			back.setBoxClass(box.getBoxClass());
			back.setName(box.getName());
			return back;
		}
		return null;
	}

	@Override
	public Class<? extends Component> getClassBySimpleClassName(final String simpleClazz) {
		final BoxConfiguration box = boxes.get(simpleClazz);
		if (box != null) {
			return box.getBoxClass();
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		final Collection<BoxConfiguration> boxes = boxLocator.getBoxes();
		for (final BoxConfiguration box : boxes) {
			registerBox(box);
		}
	}

	public void setBoxLocator(final BoxLocator boxLocator) {
		this.boxLocator = boxLocator;
	}
}
