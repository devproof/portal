package org.devproof.portal.core.module.mount.locator;

import org.devproof.portal.core.config.Locator;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Map;

/**
 * Locates all mount handlers in the spring context
 *
 * @author Carsten Hufe
 */
@Locator("mountHandlerLocator")
public class MountHandlerLocatorImpl implements MountHandlerLocator {
    private ApplicationContext context;

    @Override
    public Collection<MountHandler> getMountHandlers() {
        Map<String, MountHandler> handlers = context.getBeansOfType(MountHandler.class);
        return handlers.values();
    }

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		context = applicationContext;
	}
}
