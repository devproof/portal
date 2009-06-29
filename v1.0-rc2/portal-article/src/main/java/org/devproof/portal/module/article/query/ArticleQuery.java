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
package org.devproof.portal.module.article.query;

import org.devproof.portal.core.module.common.annotation.BeanJoin;
import org.devproof.portal.core.module.common.annotation.BeanQuery;
import org.devproof.portal.core.module.common.query.IQuery;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.query.ITagQuery;
import org.devproof.portal.module.article.entity.ArticleTagEntity;

/**
 * @author Carsten Hufe
 */
@BeanJoin("left join e.allRights vr left join e.tags t")
public class ArticleQuery implements IQuery<ArticleTagEntity>, ITagQuery<ArticleTagEntity> {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private RoleEntity role;
	private ArticleTagEntity tag;
	private String allTextFields;

	@BeanQuery("vr in(select rt from RoleEntity r join r.rights rt where r = ?)")
	public RoleEntity getRole() {
		return this.role;
	}

	public void setRole(final RoleEntity role) {
		this.role = role;
	}

	@BeanQuery("t = ?")
	public ArticleTagEntity getTag() {
		return this.tag;
	}

	public void setTag(final ArticleTagEntity tag) {
		this.tag = tag;
	}

	@BeanQuery("e.title like '%'||?||'%'" + " or e.teaser like '%'||?||'%'")
	public String getAllTextFields() {
		return this.allTextFields;
	}

	public void setAllTextFields(final String allTextFields) {
		this.allTextFields = allTextFields;
	}

	@BeanQuery("e.id = ?")
	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void clearSelection() {
		this.tag = null;
	}
}
