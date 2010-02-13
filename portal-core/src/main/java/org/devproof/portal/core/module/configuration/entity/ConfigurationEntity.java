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
package org.devproof.portal.core.module.configuration.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.UnhandledException;
import org.devproof.portal.core.module.configuration.ConfigurationConstants;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_configuration")
// selfcached
final public class ConfigurationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "conf_key")
	private String key;
	@Column(name = "conf_description")
	private String description;
	@Column(name = "conf_group")
	private String group;
	@Column(name = "conf_type")
	private String type;
	@Column(name = "conf_value", nullable = false)
	private String value;

	@Transient
	public Integer getIntegerValue() {
		return new Integer(value);
	}

	@Transient
	public void setIntegerValue(Integer value) {
		this.value = String.valueOf(value);
	}

	@Transient
	public Double getDoubleValue() {
		return new Double(value);
	}

	@Transient
	public void setDoubleValue(Double value) {
		this.value = String.valueOf(value);
	}

	@Transient
	public Boolean getBooleanValue() {
		return Boolean.valueOf(value);
	}

	@Transient
	public void setBooleanValue(Boolean value) {
		this.value = String.valueOf(value);
	}

	@Transient
	public Date getDateValue() {
		try {
			return ConfigurationConstants.DATE_FORMAT.parse(value);
		} catch (ParseException e) {
			throw new UnhandledException("The configuration date format is wrong!", e);
		}
	}

	@Transient
	public void setDateValue(Date value) {
		this.value = ConfigurationConstants.DATE_FORMAT.format(value);
	}

	// Generated stuff
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
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
		ConfigurationEntity other = (ConfigurationEntity) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		return true;
	}

}
