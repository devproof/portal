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
package org.devproof.portal.core.module.modulemgmt.service;

import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.modulemgmt.entity.ModuleLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Initializes the modules stuff and writes it to the db
 *
 * @author Carsten Hufe
 */
@Component
public class ModuleInitializer {
    private ModuleService moduleService;

    /*
	 * Rebuilds the module links in the database, protected for unit test
	 */
    @PostConstruct
	public void rebuildModuleLinks() {
        moduleService.rebuildModuleLinks();
	}

    @Autowired
    public void setModuleService(ModuleService moduleService) {
        this.moduleService = moduleService;
    }
}
