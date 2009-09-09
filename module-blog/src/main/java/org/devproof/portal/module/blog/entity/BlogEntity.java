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
package org.devproof.portal.module.blog.entity;

import java.util.ArrayList;
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
import javax.persistence.Transient;

import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.right.entity.RightEntity;

/**
 * Represents a blog entry
 * 
 * @author Carsten Hufe
 * 
 */
@Entity
@Table(name = "blog")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
final public class BlogEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "headline")
	private String headline;
	@Lob
	@Column(name = "content")
	private String content;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "blog_right_xref", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
	private List<RightEntity> allRights;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "blog_tag_xref", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
	private List<BlogTagEntity> tags;

	// Generated stuff
	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(final String headline) {
		this.headline = headline;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public List<RightEntity> getAllRights() {
		if (allRights == null) {
			allRights = new ArrayList<RightEntity>();
		}
		return allRights;
	}

	public void setAllRights(final List<RightEntity> allRights) {
		this.allRights = allRights;
	}

	@Transient
	public List<RightEntity> getViewRights() {
		return getRightsStartingWith(allRights, "blog.view");
	}

	public List<BlogTagEntity> getTags() {
		if (tags == null) {
			tags = new ArrayList<BlogTagEntity>();
		}
		return tags;
	}

	public void setTags(final List<BlogTagEntity> tags) {
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		final BlogEntity other = (BlogEntity) obj;
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
