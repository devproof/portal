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
package org.devproof.portal.core.module.common.entity;

import org.devproof.portal.core.module.right.entity.Right;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Base entity class
 *
 * @author Carsten Hufe
 */
@MappedSuperclass
public abstract class Modification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "created_by", length = 30)
    private String createdBy;
    @Column(name = "created_at")
    private Date createdAt;
    @Column(name = "modified_by", length = 30)
    private String modifiedBy;
    @Column(name = "modified_at")
    private Date modifiedAt;
    @Transient
    private boolean updateModificationData = true;

    public void copyFrom(Modification modification) {
        createdAt = modification.createdAt;
        createdBy = modification.createdBy;
        modifiedAt = modification.modifiedAt;
        modifiedBy = modification.modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public boolean isUpdateModificationData() {
        return updateModificationData;
    }

    public void setUpdateModificationData(boolean updateModificationData) {
        this.updateModificationData = updateModificationData;
    }

    @Transient
    protected List<Right> getRightsStartingWith(List<Right> rights, String prefix) {
        List<Right> back = new ArrayList<Right>();
        for (Right right : rights) {
            if (right.getRight().startsWith(prefix)) {
                back.add(right);
            }
        }
        return back;
    }
}
