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
package org.devproof.portal.core.module.right.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_right")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = CommonConstants.ENTITY_CORE_CACHE_REGION)
final public class RightEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "right_id", length = 50)
	private String right;
	@Column(name = "description")
	private String description;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "core_role_right_xref", joinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	@OrderBy("description asc")
	private List<RoleEntity> roles;

	@Transient
	private boolean selected = false;

	public RightEntity() {
	}

	public RightEntity(String right) {
		this.right = right;
	}

	// Generated stuff
	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<RoleEntity> getRoles() {
		if (roles == null) {
			roles = new ArrayList<RoleEntity>();
		}
		return roles;
	}

	public void setRoles(List<RoleEntity> roles) {
		this.roles = roles;
	}

	public boolean add(RoleEntity e) {
		return roles.add(e);
	}

	public boolean addAll(Collection<? extends RoleEntity> c) {
		return roles.addAll(c);
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean pSelected) {
		selected = pSelected;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((right == null) ? 0 : right.hashCode());
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
		RightEntity other = (RightEntity) obj;
		if (right == null) {
			if (other.right != null) {
				return false;
			}
		} else if (!right.equals(other.right)) {
			return false;
		}
		return true;
	}

}
