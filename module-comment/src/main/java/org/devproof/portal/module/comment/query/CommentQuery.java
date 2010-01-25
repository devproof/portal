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
package org.devproof.portal.module.comment.query;

import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.IQuery;

/**
 * @author Carsten Hufe
 */
public class CommentQuery implements IQuery {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String allTextFields;
	private String moduleName;
	private String moduleContentId;
	private Boolean accepted;
	private Boolean reviewed;
	private Boolean automaticBlocked;

	@BeanQuery("e.accepted = ?")
	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	@BeanQuery("e.reviewed = ?")
	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}

	@BeanQuery("e.automaticBlocked = ?")
	public Boolean getAutomaticBlocked() {
		return automaticBlocked;
	}

	public void setAutomaticBlocked(Boolean automaticBlocked) {
		this.automaticBlocked = automaticBlocked;
	}

	@BeanQuery("e.moduleName = ?")
	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@BeanQuery("e.moduleContentId = ?")
	public String getModuleContentId() {
		return moduleContentId;
	}

	public void setModuleContentId(String moduleContentId) {
		this.moduleContentId = moduleContentId;
	}

	@Override
	@BeanQuery("e.id = ?")
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	@BeanQuery("e.comment like '%'||?||'%'")
	public String getAllTextFields() {
		return allTextFields;
	}

	@Override
	public void setAllTextFields(String allTextFields) {
		this.allTextFields = allTextFields;
	}

	@Override
	public void clearSelection() {
		// nothing todo
	}
}
