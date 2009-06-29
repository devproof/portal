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
package org.devproof.portal.module.otherpage.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.right.entity.RightEntity;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "other_page")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
final public class OtherPageEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "content_id")
	private String contentId;
	@Lob
	@Column(name = "content")
	private String content;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "other_page_right_xref", joinColumns = @JoinColumn(name = "other_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
	private List<RightEntity> viewRights;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getContentId() {
		return this.contentId;
	}

	public void setContentId(final String contentId) {
		this.contentId = contentId;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public List<RightEntity> getViewRights() {
		return this.viewRights;
	}

	public void setViewRights(final List<RightEntity> viewRights) {
		this.viewRights = viewRights;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
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
		final OtherPageEntity other = (OtherPageEntity) obj;
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
