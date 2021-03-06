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
package org.devproof.portal.core.module.feed.locator;

import org.devproof.portal.core.config.Locator;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.Map;

/**
 * Locates the pages of all modules
 *
 * @author Carsten Hufe
 */
@Locator("feedProviderLocator")
public class FeedProviderLocatorImpl implements FeedProviderLocator {
    private ApplicationContext context;

    @Override
    public Collection<FeedProvider> getFeedProviders() {
        Map<String, FeedProvider> beans = context.getBeansOfType(FeedProvider.class);
        return beans.values();
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }
}
