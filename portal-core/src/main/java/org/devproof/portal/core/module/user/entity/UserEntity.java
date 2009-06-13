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
package org.devproof.portal.core.module.user.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.hibernate.annotations.Index;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_user")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
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
	private String passwordMD5;
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
	@Column(name = "last_ip", length = 15)
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
	// @Version
	// @Column(name="version")
	// private Integer version;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private RoleEntity role;
	@Transient
	private boolean guestRole = false;

	@Override
	public String toString() {
		return this.id + ": " + this.username + "(" + this.email + ")";
	}

	// generated stuff
	public String getUsername() {
		return this.username;
	}

	public void setUsername(final String username) {
		this.username = username;
	}

	public String getPasswordMD5() {
		return this.passwordMD5;
	}

	public void setPasswordMD5(final String passwordMD5) {
		this.passwordMD5 = passwordMD5;
	}

	public String getForgotPasswordCode() {
		return this.forgotPasswordCode;
	}

	public void setForgotPasswordCode(final String forgotPasswordCode) {
		this.forgotPasswordCode = forgotPasswordCode;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public Date getRegistrationDate() {
		return this.registrationDate;
	}

	public void setRegistrationDate(final Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Date getChangedAt() {
		return this.changedAt;
	}

	public void setChangedAt(final Date changedAt) {
		this.changedAt = changedAt;
	}

	public String getLastIp() {
		return this.lastIp;
	}

	public void setLastIp(final String lastIp) {
		this.lastIp = lastIp;
	}

	public Date getLastLoginAt() {
		return this.lastLoginAt;
	}

	public void setLastLoginAt(final Date lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(final Boolean active) {
		this.active = active;
	}

	public Boolean getConfirmed() {
		return this.confirmed;
	}

	public void setConfirmed(final Boolean confirmed) {
		this.confirmed = confirmed;
	}

	public String getConfirmationCode() {
		return this.confirmationCode;
	}

	public void setConfirmationCode(final String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public Date getConfirmationRequestedAt() {
		return this.confirmationRequestedAt;
	}

	public void setConfirmationRequestedAt(final Date confirmationRequestedAt) {
		this.confirmationRequestedAt = confirmationRequestedAt;
	}

	public Date getConfirmationApprovedAt() {
		return this.confirmationApprovedAt;
	}

	public void setConfirmationApprovedAt(final Date confirmationApprovedAt) {
		this.confirmationApprovedAt = confirmationApprovedAt;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	public Date getBirthday() {
		return this.birthday;
	}

	public void setBirthday(final Date birthday) {
		this.birthday = birthday;
	}

	public String getSessionId() {
		return this.sessionId;
	}

	public void setSessionId(final String sessionId) {
		this.sessionId = sessionId;
	}

	public RoleEntity getRole() {
		return this.role;
	}

	public void setRole(final RoleEntity role) {
		this.role = role;
	}

	public boolean isGuestRole() {
		return this.guestRole;
	}

	public void setGuestRole(final boolean guestRole) {
		this.guestRole = guestRole;
	}

	public Boolean getEnableContactForm() {
		return this.enableContactForm;
	}

	public void setEnableContactForm(final Boolean enableContactForm) {
		this.enableContactForm = enableContactForm;
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
		final UserEntity other = (UserEntity) obj;
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
