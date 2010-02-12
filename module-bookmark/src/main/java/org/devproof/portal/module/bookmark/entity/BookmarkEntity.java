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
package org.devproof.portal.module.bookmark.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.deadlinkcheck.entity.BaseLinkEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "bookmark")
// @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "aaa")
final public class BookmarkEntity extends BaseLinkEntity {
	private static final long serialVersionUID = 1L;

	public enum Source {
		MANUAL, DELICIOUS
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "source")
	private Source source = Source.MANUAL;
	@Column(name = "sync_username")
	private String syncUsername;
	@Column(name = "sync_hash")
	private String syncHash;
	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "bookmark_right_xref", joinColumns = @JoinColumn(name = "bookmark_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
	private List<RightEntity> allRights;
	@ManyToMany(fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name = "bookmark_tag_xref", joinColumns = @JoinColumn(name = "bookmark_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
	private List<BookmarkTagEntity> tags;

	@Transient
	public List<RightEntity> getVoteRights() {
		return getRightsStartingWith(allRights, "bookmark.vote");
	}

	@Transient
	public List<RightEntity> getVisitRights() {
		return getRightsStartingWith(allRights, "bookmark.visit");
	}

	@Transient
	public List<RightEntity> getViewRights() {
		return getRightsStartingWith(allRights, "bookmark.view");
	}

	public List<RightEntity> getAllRights() {
		if (allRights == null) {
			allRights = new ArrayList<RightEntity>();
		}
		return allRights;
	}

	public void setAllRights(List<RightEntity> allRights) {
		this.allRights = allRights;
	}

	public List<BookmarkTagEntity> getTags() {
		return tags;
	}

	public void setTags(List<BookmarkTagEntity> tags) {
		this.tags = tags;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public String getSyncUsername() {
		return syncUsername;
	}

	public void setSyncUsername(String syncUsername) {
		this.syncUsername = syncUsername;
	}

	public String getSyncHash() {
		return syncHash;
	}

	public void setSyncHash(String syncHash) {
		this.syncHash = syncHash;
	}

}
