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
package org.devproof.portal.module.bookmark.query;

import org.devproof.portal.core.module.common.annotation.BeanJoin;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.IQuery;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.query.ITagQuery;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.deadlinkcheck.query.IBrokenQuery;

/**
 * @author Carsten Hufe
 */
@BeanJoin("left join e.allRights ar left join e.tags t")
public class BookmarkQuery implements IQuery<BookmarkTagEntity>, ITagQuery<BookmarkTagEntity>, IBrokenQuery {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private RoleEntity role;
	private BookmarkTagEntity tag;
	private String allTextFields;
	private Boolean broken;

	@BeanQuery("e.id = ?")
	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	@BeanQuery("ar in(select rt from RoleEntity r join r.rights rt where r = ? and rt.right like 'bookmark.view%')")
	public RoleEntity getRole() {
		return this.role;
	}

	public void setRole(final RoleEntity role) {
		this.role = role;
	}

	@BeanQuery("t = ?")
	public BookmarkTagEntity getTag() {
		return this.tag;
	}

	public void setTag(final BookmarkTagEntity tag) {
		this.tag = tag;
	}

	@BeanQuery("(e.title like '%'||?||'%' or e.description like '%'||?||'%')")
	public String getAllTextFields() {
		return this.allTextFields;
	}

	public void setAllTextFields(final String allTextFields) {
		this.allTextFields = allTextFields;
	}

	@BeanQuery("e.broken = ?")
	public Boolean getBroken() {
		return this.broken;
	}

	public void setBroken(final Boolean broken) {
		this.broken = broken;
	}

	public void clearSelection() {
		this.tag = null;
	}
}
