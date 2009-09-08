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
package org.devproof.portal.core.module.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.devproof.portal.core.module.right.entity.RightEntity;

/**
 * Base entity class
 * 
 * @author Carsten Hufe
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "created_by", length = 30)
	private String createdBy;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "modified_by", length = 30)
	private String modifiedBy;
	@Column(name = "modified_at")
	private Date modifiedAt;

	// @Version
	// @Column(name="version")
	// private Integer version = 1;

	public void setModifiedBy(final String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedAt() {
		return modifiedAt;
	}

	public void setModifiedAt(final Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(final Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	@Transient
	protected List<RightEntity> getRightsStartingWith(final List<RightEntity> rights, final String prefix) {
		final List<RightEntity> back = new ArrayList<RightEntity>();
		for (final RightEntity right : rights) {
			if (right.getRight().startsWith(prefix)) {
				back.add(right);
			}
		}
		return back;
	}

}
