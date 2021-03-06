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
package org.devproof.portal.core.module.box.registry;

import org.apache.wicket.Component;
import org.devproof.portal.core.config.BoxConfiguration;

import java.util.List;

/**
 * Registry for boxes
 *
 * @author Carsten Hufe
 */
public interface BoxRegistry {
    /**
     * Registers a box
     *
     * @param box box to register
     */
    void registerBox(BoxConfiguration box);

    /**
     * Removes a box
     *
     * @param box box to remove
     */
    void removeBox(BoxConfiguration box);

    /**
     * Returns all registered boxes
     */
    List<BoxConfiguration> getRegisteredBoxes();

    /**
     * Returns true if the box is registered
     *
     * @param clazz Implementation class of the box
     */
    boolean isBoxClassRegistered(String clazz);

    /**
     * Returns the name of box
     *
     * @param simpleClazz clazz
     */
    String getNameBySimpleClassName(String simpleClazz);

    /**
     * Returns the name of box configuration
     *
     * @param simpleClazz Simple class name
     */
    BoxConfiguration getBoxConfigurationBySimpleClassName(String simpleClazz);

    /**
     * Returns the class by simple class name
     *
     * @param simpleClazz simple class name
     */
	Class<? extends Component> getClassBySimpleClassName(String simpleClazz);
}
