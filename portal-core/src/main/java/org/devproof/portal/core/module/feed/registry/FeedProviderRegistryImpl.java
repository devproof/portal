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
package org.devproof.portal.core.module.feed.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Page;
import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.feed.locator.FeedProviderLocator;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class FeedProviderRegistryImpl implements FeedProviderRegistry, InitializingBean {
	private PageLocator pageLocator;
	private FeedProviderLocator feedProviderLocator;
	private final Map<String, FeedProvider> feedProviders = new HashMap<String, FeedProvider>();
	private final Map<Class<? extends Page>, String> feedPaths = new HashMap<Class<? extends Page>, String>();

	@Override
	public Map<String, FeedProvider> getAllFeedProvider() {
		return Collections.unmodifiableMap(feedProviders);
	}

	@Override
	public FeedProvider getFeedProviderByPath(final String path) {
		String newPath = getPathWithoutLeadingSlash(path);
		return feedProviders.get(newPath);
	}

	@Override
	public void registerFeedProvider(final String path, final FeedProvider feedProvider) {
		String newPath = getPathWithoutLeadingSlash(path);
		if (feedProviders.containsKey(newPath)) {
			throw new IllegalArgumentException(newPath + " does already exist in the FeedProviderRegistry!");
		}
		feedProviders.put(newPath, feedProvider);
	}

	@Override
	public void removeFeedProvider(final String path) {
		String newPath = getPathWithoutLeadingSlash(path);
		feedProviders.remove(newPath);
	}

	private String getPathWithoutLeadingSlash(final String path) {
		String newPath = path;
		if (newPath.startsWith("/")) {
			newPath = path.substring(1);
		}
		return newPath;
	}

	@Override
	public String getPathByPageClass(final Class<? extends Page> pageClass) {
		return feedPaths.get(pageClass);
	}

	@Override
	public boolean hasFeedSupport(final Class<? extends Page> pageClass) {
		return feedPaths.containsKey(pageClass);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<PageConfiguration> pages = pageLocator.getPageConfigurations();
		Collection<FeedProvider> feeds = feedProviderLocator.getFeedProviders();
		for (final FeedProvider feed : feeds) {
			for (PageConfiguration page : pages) {
				if (feed.getSupportedFeedPages().contains(page.getPageClass())) {
					registerFeedProvider(page.getMountPath(), feed);
					registerFeedPath(page.getMountPath(), page.getPageClass());
				}
			}
		}
	}

	private void registerFeedPath(final String mountPath, final Class<? extends Page> pageClass) {
		String newPath = getPathWithoutLeadingSlash(mountPath);
		if (feedPaths.containsKey(pageClass)) {
			throw new IllegalArgumentException(newPath + " does already exist in the FeedProviderRegistry!");
		}
		feedPaths.put(pageClass, newPath);
	}

	@Required
	public void setPageLocator(final PageLocator pageLocator) {
		this.pageLocator = pageLocator;
	}

	@Required
	public void setFeedProviderLocator(final FeedProviderLocator feedProviderLocator) {
		this.feedProviderLocator = feedProviderLocator;
	}
}
