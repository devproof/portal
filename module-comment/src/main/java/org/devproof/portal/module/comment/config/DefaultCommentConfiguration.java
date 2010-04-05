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
package org.devproof.portal.module.comment.config;

import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.right.entity.RightEntity;

import java.util.Collection;

/**
 * @author Carsten Hufe
 */
public class DefaultCommentConfiguration implements CommentConfiguration {
    private static final long serialVersionUID = 1L;
    private String moduleName;
    private String moduleContentId;
    private Collection<RightEntity> viewRights;
    private Collection<RightEntity> writeRights;

    public Collection<RightEntity> getViewRights() {
        return viewRights;
    }

    public void setViewRights(Collection<RightEntity> viewRights) {
        this.viewRights = viewRights;
    }

    public Collection<RightEntity> getWriteRights() {
        return writeRights;
    }

    public void setWriteRights(Collection<RightEntity> writeRights) {
        this.writeRights = writeRights;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public void setModuleContentId(String moduleContentId) {
        this.moduleContentId = moduleContentId;
    }

    @Override
    public String getModuleContentId() {
        return moduleContentId;
    }

    @Override
    public String getModuleName() {
        return moduleName;
    }

    @Override
    public boolean isAllowedToWrite() {
        return PortalSession.get().hasRight(writeRights);
    }

    @Override
    public boolean isAllowedToView() {
        return PortalSession.get().hasRight(viewRights);
    }
}
