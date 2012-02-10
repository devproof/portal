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
package org.devproof.portal.core.module.mount.mapper;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.IRequestMapper;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.handler.BookmarkablePageRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.module.mount.service.MountService;

/**
 * Provides the mount logic for the whole portal
 *
 * @author Carsten Hufe
 */
public class PortalMountRequestMapper implements IRequestMapper {
    private MountService mountService;
    private IRequestMapper delegate;

    public PortalMountRequestMapper(MountService mountService, IRequestMapper delegate) {
        this.mountService = mountService;
        this.delegate = delegate;
    }

    @Override
    public IRequestHandler mapRequest(Request request) {
        // TODO oder getPath, getAbsolutPath anstelle von toString?
        if(mountService.existsPath(request.getUrl().toString())) {
            return mountService.resolveRequestHandler(request);
        }
        return delegate.mapRequest(request);
    }

    @Override
    public int getCompatibilityScore(Request request) {
        return 10000;
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler) {
        if(requestHandler instanceof BookmarkablePageRequestHandler) {
            BookmarkablePageRequestHandler bp = (BookmarkablePageRequestHandler) requestHandler;
            Class<? extends IRequestablePage> pageClass = bp.getPageClass();
            PageParameters pageParameters = bp.getPageParameters();
            Url url = mountService.urlFor(pageClass, pageParameters);
            if(url != null) {
                return url;
            }
        }
        return delegate.mapHandler(requestHandler);
    }
}
