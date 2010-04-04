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
package org.devproof.portal.core.module.user.entity;

import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.user.UserConstants;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_user")
@CacheQuery(region = UserConstants.QUERY_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = UserConstants.ENTITY_CACHE_REGION)
final public class UserEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Index(name = "username")
	@Column(name = "username", unique = true, length = 30)
	private String username;
	@Column(name = "password", nullable = false)
	private String encryptedPassword;
	@Column(name = "forgot_code", nullable = true)
	private String forgotPasswordCode;
	@Column(name = "firstname", length = 100, nullable = true)
	private String firstname;
	@Column(name = "lastname", length = 100, nullable = true)
	private String lastname;
	@Column(name = "birthday", nullable = true)
	private Date birthday;
	@Column(name = "email", length = 100, nullable = false)
	private String email;
	@Column(name = "enable_contact_form")
	private Boolean enableContactForm = Boolean.FALSE;
	@Column(name = "session_id", nullable = true)
	private String sessionId;
	@Column(name = "reg_date", nullable = false)
	private Date registrationDate = PortalUtil.now();
	@Column(name = "changed_at")
	private Date changedAt;
	// IPv6 length
	@Column(name = "last_ip", length = 39)
	private String lastIp;
	@Column(name = "last_login_at")
	private Date lastLoginAt;
	@Column(name = "active", nullable = false)
	private Boolean active = Boolean.TRUE;
	@Column(name = "confirmed", nullable = false)
	private Boolean confirmed = Boolean.TRUE;
	@Column(name = "confirmation_code")
	private String confirmationCode;
	@Column(name = "confirm_req_at")
	private Date confirmationRequestedAt;
	@Column(name = "confirm_app_at")
	private Date confirmationApprovedAt;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private RoleEntity role;
	@Transient
	private boolean guestRole = false;

	@Override
	public String toString() {
		return id + ": " + username + "(" + email + ")";
	}

	// generated stuff
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	@Transient
	public void setPlainPassword(String plainPassword) {
		this.encryptedPassword = PortalUtil.generateMd5(plainPassword);
	}

	@Transient
	public boolean equalPassword(String plainPassword) {
		return PortalUtil.generateMd5(plainPassword).equals(encryptedPassword);
	}

	public String getForgotPasswordCode() {
		return forgotPasswordCode;
	}

	public void setForgotPasswordCode(String forgotPasswordCode) {
		this.forgotPasswordCode = forgotPasswordCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(Date changedAt) {
		this.changedAt = changedAt;
	}

	public String getLastIp() {
		return lastIp;
	}

	public void setLastIp(String lastIp) {
		this.lastIp = lastIp;
	}

	public Date getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(Date lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public Date getConfirmationRequestedAt() {
		return confirmationRequestedAt;
	}

	public void setConfirmationRequestedAt(Date confirmationRequestedAt) {
		this.confirmationRequestedAt = confirmationRequestedAt;
	}

	public Date getConfirmationApprovedAt() {
		return confirmationApprovedAt;
	}

	public void setConfirmationApprovedAt(Date confirmationApprovedAt) {
		this.confirmationApprovedAt = confirmationApprovedAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public RoleEntity getRole() {
		return role;
	}

	public void setRole(RoleEntity role) {
		this.role = role;
	}

	public boolean isGuestRole() {
		return guestRole;
	}

	public void setGuestRole(boolean guestRole) {
		this.guestRole = guestRole;
	}

	public Boolean getEnableContactForm() {
		return enableContactForm;
	}

	public void setEnableContactForm(Boolean enableContactForm) {
		this.enableContactForm = enableContactForm;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		UserEntity other = (UserEntity) obj;
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
