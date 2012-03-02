/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.blog.query;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.role.entity.Role;
import org.devproof.portal.core.module.tag.TagConstants;

/**
 * @author Carsten Hufe
 */
public class BlogQuery implements SearchQuery {
	private static final long serialVersionUID = 1L;
	private static final String ID_PARAM = "id";
	private static final String SEARCH_PARAM = "search";
	private Integer id;
	private Role role;
	private String tagname;
	private String allTextFields;

	public BlogQuery(PageParameters params) {
		id = params.get(ID_PARAM).toOptionalInteger();
		allTextFields = params.get(SEARCH_PARAM).toOptionalString();
		tagname = PortalUtil.getParameterAsString(TagConstants.TAG_PARAM);
	}

	@BeanQuery("exists(from Blog b left join b.allRights ar "
			+ "where ar in(select r from Right r join r.roles rt where rt = ? and r.right like 'blog.view%') and b = e)")
	public Role getRole() {
		if (role == null) {
			PortalSession session = PortalSession.get();
			if (!session.hasRight("blog.view")) {
				role = session.getRole();
			}
		}
		return role;
	}

	@BeanQuery("exists(from Blog b left join b.tags t where t.tagname = ? and b = e)")
	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	@BeanQuery("e.headline like '%'||?||'%'" + " or e.content like '%'||?||'%'")
	public String getAllTextFields() {
		return allTextFields;
	}

	public void setAllTextFields(String allTextFields) {
		this.allTextFields = allTextFields;
	}

	@BeanQuery("e.id = ?")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public PageParameters getPageParameters() {
		PageParameters params = new PageParameters();
		if (StringUtils.isNotBlank(tagname)) {
			params.put(TagConstants.TAG_PARAM, tagname);
		}
		if (StringUtils.isNotBlank(allTextFields)) {
			params.put(SEARCH_PARAM, allTextFields);
		}
		return params;
	}
}
