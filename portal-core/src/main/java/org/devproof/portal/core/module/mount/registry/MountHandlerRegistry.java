/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.core.module.mount.registry;

import java.util.Map;

/**
 * Registry for mount handler
 *
 * @author Carsten Hufe
 */
public interface MountHandlerRegistry {
    /**
     * Registers a new mount handler
     *
     * @param handlerKey handler key
     * @param mountHandler mount handler
     */
    void registerMountHandler(String handlerKey, MountHandler mountHandler);

    /**
     * Removes a mount handler
     *
     * @param handlerKey handler key
     */
    void removeMountHandler(String handlerKey);

    /**
     * Resolve a mount handler
     *
     * @param handlerKey handler key
     * @return matching mount handler
     */
    MountHandler getMountHandler(String handlerKey);

    /**
     * Checks if a mount handler exists
     *
     * @param handlerKey handler key
     * @return true if exists
     */
    boolean isMountHandlerAvailable(String handlerKey);

    /**
     * Returns all mount handler
     *
     * @return all registered mount handlers
     */
	Map<String, MountHandler> getRegisteredMountHandlers();
}
