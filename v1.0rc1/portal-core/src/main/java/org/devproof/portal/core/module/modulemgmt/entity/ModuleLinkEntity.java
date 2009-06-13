/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.devproof.portal.core.module.modulemgmt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.devproof.portal.core.module.common.entity.BaseEntity;

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
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
final public class ModuleLinkEntity extends BaseEntity implements Comparable<ModuleLinkEntity> {

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
		return this.moduleName;
	}

	public void setModuleName(final String moduleName) {
		this.moduleName = moduleName;
	}

	public String getPageName() {
		return this.pageName;
	}

	public void setPageName(final String pageName) {
		this.pageName = pageName;
	}

	public Integer getSort() {
		return this.sort;
	}

	public void setSort(final Integer sort) {
		this.sort = sort;
	}

	public LinkType getLinkType() {
		return this.linkType;
	}

	public void setLinkType(final LinkType linkType) {
		this.linkType = linkType;
	}

	public Boolean getVisible() {
		return this.visible;
	}

	public void setVisible(final Boolean visible) {
		this.visible = visible;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.linkType == null) ? 0 : this.linkType.hashCode());
		result = prime * result + ((this.pageName == null) ? 0 : this.pageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
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
		if (this.linkType == null) {
			if (other.linkType != null) {
				return false;
			}
		} else if (!this.linkType.equals(other.linkType)) {
			return false;
		}
		if (this.pageName == null) {
			if (other.pageName != null) {
				return false;
			}
		} else if (!this.pageName.equals(other.pageName)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final ModuleLinkEntity arg0) {
		return this.sort.compareTo(arg0.getSort());
	}
}
