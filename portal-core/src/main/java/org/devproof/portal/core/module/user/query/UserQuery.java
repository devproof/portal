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
package org.devproof.portal.core.module.user.query;

import org.devproof.portal.core.module.common.annotation.BeanQuery;

import java.io.Serializable;

/**
 * @author Carsten Hufe
 */
public class UserQuery implements Serializable {
    private static final long serialVersionUID = 1L;

    private String allnames;
    private Boolean active;
    private Boolean confirmed;

    @BeanQuery("e.username like '%'||?||'%'" + " or e.firstname like '%'||?||'%'" + " or e.lastname like '%'||?||'%'" + " or e.role.description like '%'||?||'%'")
    public String getAllnames() {
        return allnames;
    }

    public void setAllnames(String allnames) {
        this.allnames = allnames;
    }

    @BeanQuery("e.active = ?")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @BeanQuery("e.confirmed = ?")
    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }
}
