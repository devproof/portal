/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.devproof.portal.core.module.modulemgmt.entity.ModuleLinkEntity.LinkType;

/**
 * @author Carsten Hufe
 */
public class ModuleLinkId implements Serializable {
	private static final long serialVersionUID = 1L;
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "page_name")
	private String pageName;
	@Column(name = "link_type")
	private LinkType linkType;

	public ModuleLinkId() {
	}

	public ModuleLinkId(final String pageName, final LinkType linkType) {
		this.pageName = pageName;
		this.linkType = linkType;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(final String pageName) {
		this.pageName = pageName;
	}

	public LinkType getLinkType() {
		return linkType;
	}

	public void setLinkType(final LinkType linkType) {
		this.linkType = linkType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((linkType == null) ? 0 : linkType.hashCode());
		result = prime * result + ((pageName == null) ? 0 : pageName.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ModuleLinkId other = (ModuleLinkId) obj;
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
}