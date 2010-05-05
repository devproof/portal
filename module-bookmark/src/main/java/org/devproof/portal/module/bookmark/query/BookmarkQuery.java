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
package org.devproof.portal.module.bookmark.query;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.SearchQuery;
import org.devproof.portal.core.module.common.util.PortalUtil;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.module.bookmark.BookmarkConstants;

/**
 * @author Carsten Hufe
 */
public class BookmarkQuery implements SearchQuery {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private static final String SEARCH_PARAM = "search";
	private static final String BROKEN_PARAM = "broken";
	private static final String ID_PARAM = "id";
	private Integer id;
	private RoleEntity role;
	private String tagname;
	private String allTextFields;
	private Boolean broken;
	private Boolean author;

	public BookmarkQuery() {
		InjectorHolder.getInjector().inject(this);
		id = PortalUtil.getParameterAsInteger(ID_PARAM);
		allTextFields = PortalUtil.getParameterAsString(SEARCH_PARAM);
		tagname = PortalUtil.getParameterAsString(TagConstants.TAG_PARAM);
		if (isAuthor()) {
			broken = PortalUtil.getParameterAsBoolean(BROKEN_PARAM);
		} else if (configurationService.findAsBoolean(BookmarkConstants.CONF_BOOKMARK_HIDE_BROKEN)) {
			broken = false;
		}
	}

	private boolean isAuthor() {
		if (author == null) {
			PortalSession session = PortalSession.get();
			author = session.hasRight(BookmarkConstants.AUTHOR_RIGHT);
		}
		return author;
	}

	@BeanQuery("e.id = ?")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@BeanQuery("exists(from BookmarkEntity b left join b.allRights ar "
			+ "where ar in(select r from RightEntity r join r.roles rt where rt = ? and r.right like 'bookmark.view%') and b = e)")
	public RoleEntity getRole() {
		if (role == null) {
			PortalSession session = PortalSession.get();
			if (!session.hasRight("bookmark.view")) {
				role = session.getRole();
			}
		}
		return role;
	}

	@BeanQuery("exists(from BookmarkEntity b left join b.tags t where t.tagname = ? and b = e)")
	public String getTagname() {
		return tagname;
	}

	public void setTagname(String tagname) {
		this.tagname = tagname;
	}

	@BeanQuery("(e.title like '%'||?||'%' or e.description like '%'||?||'%')")
	public String getAllTextFields() {
		return allTextFields;
	}

	public void setAllTextFields(String allTextFields) {
		this.allTextFields = allTextFields;
		this.tagname = null;
	}

	@BeanQuery("e.broken = ?")
	public Boolean getBroken() {
		return broken;
	}

	public void setBroken(Boolean broken) {
		this.broken = broken;
		this.tagname = null;
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
		if (isAuthor() && broken != null) {
			params.put(BROKEN_PARAM, broken);
		}
		return params;
	}
}
