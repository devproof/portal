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
