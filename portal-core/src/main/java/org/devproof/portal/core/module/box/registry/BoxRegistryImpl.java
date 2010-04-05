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
package org.devproof.portal.core.module.box.registry;

import org.apache.wicket.Component;
import org.devproof.portal.core.config.BoxConfiguration;
import org.devproof.portal.core.module.box.locator.BoxLocator;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

/**
 * @author Carsten Hufe
 */
public class BoxRegistryImpl implements BoxRegistry, InitializingBean {
	private BoxLocator boxLocator;
	private final Map<String, BoxConfiguration> boxes = new HashMap<String, BoxConfiguration>();

	@Override
	public String getNameBySimpleClassName(String clazz) {
		BoxConfiguration box = boxes.get(clazz);
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
	public boolean isBoxClassRegistered(String clazz) {
		return boxes.containsKey(clazz);
	}

	@Override
	public void registerBox(BoxConfiguration box) {
		boxes.put(box.getBoxClass().getSimpleName(), box);
	};

	@Override
	public void removeBox(BoxConfiguration box) {
		boxes.remove(box.getBoxClass().getSimpleName());
	}

	@Override
	public BoxConfiguration getBoxConfigurationBySimpleClassName(String simpleClazz) {
		BoxConfiguration box = boxes.get(simpleClazz);
		if (box != null) {
			BoxConfiguration back = new BoxConfiguration();
			back.setBoxClass(box.getBoxClass());
			back.setName(box.getName());
			return back;
		}
		return null;
	}

	@Override
	public Class<? extends Component> getClassBySimpleClassName(String simpleClazz) {
		BoxConfiguration box = boxes.get(simpleClazz);
		if (box != null) {
			return box.getBoxClass();
		}
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<BoxConfiguration> boxes = boxLocator.getBoxes();
		for (BoxConfiguration box : boxes) {
			registerBox(box);
		}
	}

	public void setBoxLocator(BoxLocator boxLocator) {
		this.boxLocator = boxLocator;
	}
}
