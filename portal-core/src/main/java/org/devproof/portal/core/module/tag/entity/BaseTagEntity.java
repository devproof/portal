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
package org.devproof.portal.core.module.tag.entity;

import org.devproof.portal.core.module.common.entity.Modification;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.List;

/**
 * Base class for tags
 *
 * @author Carsten Hufe
 */
@MappedSuperclass
public abstract class BaseTagEntity<T> extends Modification {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "tagname")
    private String tagname;

    public String getTagname() {
        return this.tagname;
    }

    public void setTagname(String tagname) {
        this.tagname = tagname != null ? tagname.toLowerCase() : null;
    }

    public abstract void setReferencedObjects(List<T> refObjs);

    public abstract List<?> getReferencedObjects();

    @Override
    public String toString() {
        return String.format("Tag: %s", this.tagname);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((this.tagname == null) ? 0 : this.tagname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        BaseTagEntity<?> other = (BaseTagEntity<?>) obj;
        if (this.tagname == null) {
            if (other.tagname != null) {
                return false;
            }
        } else if (!this.tagname.equals(other.tagname)) {
            return false;
        }
        return true;
    }
}
