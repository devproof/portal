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

import org.devproof.portal.core.config.PageConfiguration;
import org.devproof.portal.core.module.common.locator.PageLocator;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author Carsten Hufe
 */
public class FeedProviderRegistryImpl implements FeedProviderRegistry, InitializingBean {
	private PageLocator pageLocator;
	private final Map<String, FeedProvider> feedProviders = new HashMap<String, FeedProvider>();

	@Override
	public Map<String, FeedProvider> getAllFeedProvider() {
		return Collections.unmodifiableMap(this.feedProviders);
	}

	@Override
	public FeedProvider getFeedProviderByPath(String path) {
		String newPath = getPathWithoutLeadingSlash(path);
		return this.feedProviders.get(newPath);
	}

	@Override
	public void registerFeedProvider(String path, FeedProvider feedProvider) {
		String newPath = getPathWithoutLeadingSlash(path);
		if (this.feedProviders.containsKey(newPath)) {
			throw new IllegalArgumentException(newPath + " does already exist in the FeedProviderRegistry!");
		}
		this.feedProviders.put(newPath, feedProvider);
	}

	@Override
	public void removeFeedProvider(String path) {
		String newPath = getPathWithoutLeadingSlash(path);
		this.feedProviders.remove(newPath);
	}

	private String getPathWithoutLeadingSlash(String path) {
		String newPath = path;
		if (newPath.startsWith("/")) {
			newPath = path.substring(1);
		}
		return newPath;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Collection<PageConfiguration> confs = this.pageLocator.getPageConfigurations();
		for (PageConfiguration conf : confs) {
			if (conf.getFeedProvider() != null) {
				registerFeedProvider(conf.getMountPath(), conf.getFeedProvider());
			}
		}
	}

	public void setPageLocator(PageLocator pageLocator) {
		this.pageLocator = pageLocator;
	}
}
