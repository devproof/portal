/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.comment.query;

import org.apache.wicket.PageParameters;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.comment.CommentConstants;

/**
 * @author Carsten Hufe
 */
public class CommentQuery implements SearchQuery {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private Integer id;
	private String allTextFields;
	private String moduleName;
	private String moduleContentId;
	private Boolean accepted;
	private Boolean rejected;
	private Boolean reviewed;
	private Boolean automaticBlocked;
	private Boolean author;

	public CommentQuery() {
		InjectorHolder.getInjector().inject(this);
		boolean showOnlyReviewed = configurationService.findAsBoolean(CommentConstants.CONF_COMMENT_SHOW_ONLY_REVIEWED);
        if(!isAuthor()) {            
            if (showOnlyReviewed) {
                reviewed = Boolean.TRUE;
                accepted = Boolean.TRUE;
            } else {
                rejected = Boolean.FALSE;
            }
        }
		if (!isAuthor()) {
			automaticBlocked = Boolean.FALSE;
		}
	}

	public CommentQuery(Integer id) {
		this.id = id;
	}

	private boolean isAuthor() {
		if (author == null) {
			PortalSession session = PortalSession.get();
			author = session.hasRight(CommentConstants.AUTHOR_RIGHT);
		}
		return author.booleanValue();
	}

	@BeanQuery("e.accepted = ?")
	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	@BeanQuery("((e.accepted = true and e.reviewed = true) or e.reviewed = false)")
	public Boolean getRejected() {
		return rejected;
	}

	public void setRejected(Boolean rejected) {
		this.rejected = rejected;
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

	@BeanQuery("e.id = ?")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@BeanQuery("e.comment like '%'||?||'%'")
	public String getAllTextFields() {
		return allTextFields;
	}

	public void setAllTextFields(String allTextFields) {
		this.allTextFields = allTextFields;
	}

	@Override
	public PageParameters getPageParameters() {
        // not required for comments
		return new PageParameters();
	}
}
