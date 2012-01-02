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

/**
 * Provides the mount logic for the whole portal
 *
 * @author Carsten Hufe
 */
public class PortalMountRequestMapper implements IRequestMapper {
    @Override
    public IRequestHandler mapRequest(Request request) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getCompatibilityScore(Request request) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Url mapHandler(IRequestHandler requestHandler) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
