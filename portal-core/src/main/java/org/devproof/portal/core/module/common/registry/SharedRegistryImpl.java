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
package org.devproof.portal.core.module.common.registry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Carsten Hufe
 */
public class SharedRegistryImpl implements SharedRegistry {
	private final Map<String, Object> resources = new HashMap<String, Object>();

	@Override
	public Map<String, ?> getRegisteredResources() {
		return Collections.unmodifiableMap(resources);
	}

	@Override
	public <T> T getResource(String resourceKey) {
		@SuppressWarnings("unchecked")
		T back = (T) resources.get(resourceKey);
		return back;
	}

	@Override
	public void registerResource(String resourceKey, Object resource) {
		resources.put(resourceKey, resource);
	}

	@Override
	public void removeResource(String resourceKey) {
		resources.remove(resourceKey);
	}

	@Override
	public boolean isResourceAvailable(String resourceKey) {
		return resources.containsKey(resourceKey);
	}
}
