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
package org.devproof.portal.core.module.mount.registry;

import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.config.Registry;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.mount.locator.MountHandlerLocator;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Carsten Hufe
 */
@Registry("mountHandlerRegistry")
public class MountHandlerRegistryImpl implements MountHandlerRegistry {
    private MountHandlerLocator mountHandlerLocator;
    private final Map<String, MountHandler> mountHandlers = new HashMap<String, MountHandler>();

    @Override
    public void registerMountHandler(String handlerKey, MountHandler mountHandler) {
        mountHandlers.put(handlerKey, mountHandler);
    }

    @Override
    public void removeMountHandler(String handlerKey) {
        mountHandlers.remove(handlerKey);
    }

    @Override
    public MountHandler getMountHandler(String handlerKey) {
        return mountHandlers.get(handlerKey);
    }

    @Override
    public boolean isMountHandlerAvailable(String handlerKey) {
        return mountHandlers.containsKey(handlerKey);
    }

    @Override
    public Map<String, MountHandler> getRegisteredMountHandlers() {
        return mountHandlers;
    }

    @PostConstruct
    public void afterPropertiesSet() {
        Collection<MountHandler> handlers = mountHandlerLocator.getMountHandlers();
        for (MountHandler handler : handlers) {
            registerMountHandler(handler.getHandlerKey(), handler);
        }
    }

    @Autowired
    public void setMountHandlerLocator(MountHandlerLocator mountHandlerLocator) {
        this.mountHandlerLocator = mountHandlerLocator;
    }
}
