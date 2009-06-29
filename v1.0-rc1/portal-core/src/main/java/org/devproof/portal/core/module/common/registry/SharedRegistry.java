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
package org.devproof.portal.core.module.common.registry;

import java.util.Map;

/**
 * Registry for shared resources between modules
 * 
 * @author Carsten Hufe
 */
public interface SharedRegistry {
	/**
	 * Register a shared resource
	 * 
	 * @param resourceKey
	 *            resource key
	 * @param resource
	 *            resource to register
	 */
	public void registerResource(String resourceKey, Object resource);

	/**
	 * Remove a registered resource
	 * 
	 * @param resourceKey
	 *            resource key
	 */
	public void removeResource(String resourceKey);

	/**
	 * Returns a shared resource by the key
	 * 
	 * @param <T>
	 *            return type of resource
	 * @param resourceKey
	 *            resource key
	 * @return shared resource
	 */
	public <T> T getResource(String resourceKey);

	/**
	 * Returns true if a resource is available
	 * 
	 * @param resourceKey
	 *            resource key
	 * @return true if available
	 */
	public boolean isResourceAvailable(String resourceKey);

	/**
	 * Returns all registered resources
	 * 
	 * @return map with registered resources
	 */
	public Map<String, ?> getRegisteredResources();
}
