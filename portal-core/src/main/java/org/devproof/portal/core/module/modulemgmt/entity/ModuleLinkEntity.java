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
package org.devproof.portal.core.module.modulemgmt.entity;

import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.modulemgmt.query.ModuleLinkQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * represents one module link (top navigation, page administration, global
 * admistration). The entity is required to sort the links of the different
 * modules.
 *
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_module_link")
@IdClass(ModuleLinkId.class)
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = CommonConstants.ENTITY_CORE_CACHE_REGION)
@RegisterGenericDataProvider(value = "moduleLinkDataProvider", sortProperty = "sort", countQuery = "count(*)", queryClass = ModuleLinkQuery.class)
public class ModuleLinkEntity extends BaseEntity implements Comparable<ModuleLinkEntity> {

    private static final long serialVersionUID = 1L;

    public enum LinkType {
        TOP_NAVIGATION, GLOBAL_ADMINISTRATION, PAGE_ADMINISTRATION
    }

    @Id
    @Column(name = "page_name")
    private String pageName;
    @Id
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "link_type")
    private LinkType linkType;
    @Column(name = "module_name", nullable = false)
    private String moduleName;
    @Column(name = "visible", nullable = false)
    private Boolean visible;
    @Column(name = "sort", nullable = false)
    private Integer sort;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
        result = prime * result + ((pageName == null) ? 0 : pageName.hashCode());
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
        ModuleLinkEntity other = (ModuleLinkEntity) obj;
        if (linkType == null) {
            if (other.linkType != null) {
                return false;
            }
        } else if (!linkType.equals(other.linkType)) {
            return false;
        }
        if (pageName == null) {
            if (other.pageName != null) {
                return false;
            }
        } else if (!pageName.equals(other.pageName)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(ModuleLinkEntity arg0) {
        return sort.compareTo(arg0.getSort());
    }
}
