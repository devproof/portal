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
package org.devproof.portal.module.download.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.deadlinkcheck.entity.BaseLinkEntity;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "download")
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
final public class DownloadEntity extends BaseLinkEntity {

	private static final long serialVersionUID = 1L;
	@Column(name = "software_version")
	private String softwareVersion;
	@Column(name = "download_size")
	private String downloadSize;
	@Column(name = "manufacturer_homepage")
	private String manufacturerHomepage;
	@Column(name = "manufacturer")
	private String manufacturer;
	@Column(name = "licence")
	private String licence;
	@Column(name = "price")
	private String price;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "download_right_xref", joinColumns = @JoinColumn(name = "download_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
	private List<RightEntity> allRights;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "download_tag_xref", joinColumns = @JoinColumn(name = "download_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
	private List<DownloadTagEntity> tags;

	@Transient
	public List<RightEntity> getVoteRights() {
		return getRightsStartingWith(this.allRights, "download.vote");
	}

	@Transient
	public List<RightEntity> getDownloadRights() {
		return getRightsStartingWith(this.allRights, "download.download");
	}

	@Transient
	public List<RightEntity> getViewRights() {
		return getRightsStartingWith(this.allRights, "download.view");
	}

	public String getSoftwareVersion() {
		return this.softwareVersion;
	}

	public void setSoftwareVersion(final String softwareVersion) {
		this.softwareVersion = softwareVersion;
	}

	public String getDownloadSize() {
		return this.downloadSize;
	}

	public void setDownloadSize(final String downloadSize) {
		this.downloadSize = downloadSize;
	}

	public String getManufacturerHomepage() {
		return this.manufacturerHomepage;
	}

	public void setManufacturerHomepage(final String manufacturerHomepage) {
		this.manufacturerHomepage = manufacturerHomepage;
	}

	public String getLicence() {
		return this.licence;
	}

	public void setLicence(final String licence) {
		this.licence = licence;
	}

	public String getPrice() {
		return this.price;
	}

	public void setPrice(final String price) {
		this.price = price;
	}

	public String getManufacturer() {
		return this.manufacturer;
	}

	public void setManufacturer(final String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public List<RightEntity> getAllRights() {
		return this.allRights;
	}

	public void setAllRights(final List<RightEntity> allRights) {
		this.allRights = allRights;
	}

	public List<DownloadTagEntity> getTags() {
		return this.tags;
	}

	public void setTags(final List<DownloadTagEntity> tags) {
		this.tags = tags;
	}
}
