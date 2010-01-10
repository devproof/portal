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
package org.devproof.portal.module.comment.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.devproof.portal.core.module.common.entity.BaseEntity;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "comment")
final public class CommentEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "guest_name", length = 50)
	private String guestName;
	@Column(name = "guest_email", length = 50)
	private String guestEmail;
	@Lob
	@Column(name = "comment")
	private String comment;
	// ipv6 length
	@Column(name = "ip_address", length = 39)
	private String ipAddress;
	@Column(name = "number_of_blames")
	private Integer numberOfBlames = 0;
	@Column(name = "reviewed")
	private Boolean reviewed = Boolean.FALSE;
	@Column(name = "visible")
	private Boolean visible = Boolean.TRUE;
	@Column(name = "automatic_blocked")
	private Boolean automaticBlocked = Boolean.FALSE;
	@Column(name = "module_name", length = 20)
	private String moduleName;
	@Column(name = "module_content_id", length = 20)
	private String moduleContentId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleContentId() {
		return moduleContentId;
	}

	public void setModuleContentId(String moduleContentId) {
		this.moduleContentId = moduleContentId;
	}

	public Integer getNumberOfBlames() {
		return numberOfBlames;
	}

	public void setNumberOfBlames(Integer numberOfBlames) {
		this.numberOfBlames = numberOfBlames;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getGuestName() {
		return guestName;
	}

	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestEmail() {
		return guestEmail;
	}

	public void setGuestEmail(String guestEmail) {
		this.guestEmail = guestEmail;
	}

	public Boolean getAutomaticBlocked() {
		return automaticBlocked;
	}

	public void setAutomaticBlocked(Boolean automaticBlocked) {
		this.automaticBlocked = automaticBlocked;
	}
}
