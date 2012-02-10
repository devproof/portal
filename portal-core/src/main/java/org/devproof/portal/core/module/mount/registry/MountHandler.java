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

import org.apache.wicket.Page;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.module.mount.entity.MountPoint;

/**
 * Mount handler handles global defined URLs for a module
 *
 * @author Carsten Hufe
 */
public interface MountHandler {
    /**
     * Builds the request target
     *
     * @param requestedUrl original requested URL
     * @param mountPoint mount point
     * @return wicket request handler
     */
    IRequestHandler getRequestHandler(String requestedUrl, MountPoint mountPoint);

    /**
     * @return module handler key e.g. "article"
     */
    String getHandlerKey();

    /**
     * If a MountHandler can handle the page
     *
     * @param pageClazz wicket page class
     * @param pageParameters page params
     * @return true if it can handle
     */
    boolean canHandlePageClass(Class<? extends IRequestablePage> pageClazz, PageParameters pageParameters);

    /**
     * Returns a URL for a wicket page
     *
     * @param pageClazz page class
     * @param params page parameter
     * @return matching URL
     */
    Url urlFor(Class<? extends IRequestablePage> pageClazz, PageParameters params);
}
