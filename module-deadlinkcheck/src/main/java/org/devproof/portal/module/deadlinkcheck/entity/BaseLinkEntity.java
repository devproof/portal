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
package org.devproof.portal.module.deadlinkcheck.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.devproof.portal.core.module.common.entity.BaseEntity;

/**
 * Base entity for bookmarks and downloads
 * 
 * @author Carsten Hufe
 */
@MappedSuperclass
public abstract class BaseLinkEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "title")
	private String title;
	@Lob
	@Column(name = "description")
	private String description;
	@Column(name = "url")
	private String url;
	@Column(name = "hits")
	private Integer hits = 0;
	@Column(name = "number_of_votes")
	private Integer numberOfVotes = 0;
	@Column(name = "sum_of_rating")
	private Integer sumOfRating = 0;
	@Column(name = "broken")
	private Boolean broken = Boolean.FALSE;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Integer getHits() {
		return hits;
	}

	public void setHits(final Integer hits) {
		this.hits = hits;
	}

	public Integer getNumberOfVotes() {
		return numberOfVotes;
	}

	public void setNumberOfVotes(final Integer numberOfVotes) {
		this.numberOfVotes = numberOfVotes;
	}

	public Integer getSumOfRating() {
		return sumOfRating;
	}

	public void setSumOfRating(final Integer sumOfRating) {
		this.sumOfRating = sumOfRating;
	}

	public Boolean getBroken() {
		return broken;
	}

	public void setBroken(final Boolean broken) {
		this.broken = broken;
	}

	@Transient
	public Double getCalculatedRating() {
		double back = ((double) sumOfRating) / ((double) numberOfVotes);
		if (Double.isInfinite(back) || Double.isNaN(back)) {
			back = 0d;
		}
		return back;
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
		BaseLinkEntity other = (BaseLinkEntity) obj;
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
