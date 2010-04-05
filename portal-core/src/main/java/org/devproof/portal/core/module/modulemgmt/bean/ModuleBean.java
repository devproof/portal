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
package org.devproof.portal.core.module.modulemgmt.bean;

import org.devproof.portal.core.config.ModuleConfiguration;

/**
 * @author Carsten Hufe
 */
public class ModuleBean {
    private ModuleConfiguration configuration;
    private String location;

    public ModuleConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ModuleConfiguration configuration) {
        this.configuration = configuration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}