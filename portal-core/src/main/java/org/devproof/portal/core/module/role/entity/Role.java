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
package org.devproof.portal.core.module.role.entity;

import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.Modification;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.core.module.role.query.RoleQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_role")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = CommonConstants.ENTITY_CORE_CACHE_REGION)
@RegisterGenericDataProvider(value = "roleDataProvider", sortProperty = "description", queryClass = RoleQuery.class)
public class Role extends Modification {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "description")
    private String description;
    @Column(name = "active", nullable = false)
    private Boolean active;
    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "core_role_right_xref", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
    @OrderBy("description asc")
    private List<Right> rights;

    // Generated stuff

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<Right> getRights() {
        if (rights == null) {
            rights = new ArrayList<Right>();
        }
        return rights;
    }

    public void setRights(List<Right> rights) {
        this.rights = rights;
    }

    public boolean add(Right e) {
        return rights.add(e);
    }

    public boolean addAll(Collection<? extends Right> c) {
        return rights.addAll(c);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        Role other = (Role) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
