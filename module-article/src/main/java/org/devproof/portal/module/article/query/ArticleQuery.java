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
package org.devproof.portal.module.article.query;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.TagConstants;

/**
 * @author Carsten Hufe
 */
public class ArticleQuery implements SearchQuery {
	private static final long serialVersionUID = 1L;
	private static final String ID_PARAM = "id";
	private static final String SEARCH_PARAM = "search";
	private Integer id;
	private RoleEntity role;
	private String allTextFields;
	private String tagname;

	public ArticleQuery() {
		id = PortalUtil.getParameterAsInteger(ID_PARAM);
		allTextFields = PortalUtil.getParameterAsString(SEARCH_PARAM);
		tagname = PortalUtil.getParameterAsString(TagConstants.TAG_PARAM);
	}

	@BeanQuery("exists(from Article a left join a.allRights ar "
			+ "where ar in(select r from Right r join r.roles rt where rt = ? and r.right like 'article.view%') and a = e)")
	public RoleEntity getRole() {
		if (role == null) {
			PortalSession session = PortalSession.get();
			if (!session.hasRight("article.view")) {
				role = session.getRole();
			}
		}
		return role;
	}

	@BeanQuery("exists(from Article a left join a.tags t where t.tagname = ? and a = e)")
	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	@BeanQuery("e.title like '%'||?||'%'" + " or e.teaser like '%'||?||'%'")
	public String getAllTextFields() {
		return this.allTextFields;
	}

	public void setAllTextFields(String allTextFields) {
		this.allTextFields = allTextFields;
		this.tagname = null;
	}

	@BeanQuery("e.id = ?")
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public PageParameters getPageParameters() {
		PageParameters params = new PageParameters();
		if (StringUtils.isNotBlank(allTextFields)) {
			params.put(SEARCH_PARAM, allTextFields);
		}
		if (StringUtils.isNotBlank(tagname)) {
			params.put(TagConstants.TAG_PARAM, tagname);
		}
		return params;
	}
}
