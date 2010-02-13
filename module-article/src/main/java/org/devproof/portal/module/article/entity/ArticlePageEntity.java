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
package org.devproof.portal.module.article.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Carsten Hufe
 */
@Entity
@IdClass(ArticlePageId.class)
@Table(name = "article_page")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "entity.content")
final public class ArticlePageEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "content_id")
	private String contentId;
	@Id
	@Column(name = "page")
	private Integer page;
	@Lob
	@Column(name = "content")
	private String content;
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "article_id")
	private ArticleEntity article;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArticleEntity getArticle() {
		return article;
	}

	public void setArticle(ArticleEntity article) {
		this.article = article;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public int hashCode() {
		int prime = 31;
		int result = 1;
		result = prime * result + ((contentId == null) ? 0 : contentId.hashCode());
		result = prime * result + ((page == null) ? 0 : page.hashCode());
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
		ArticlePageEntity other = (ArticlePageEntity) obj;
		if (contentId == null) {
			if (other.contentId != null) {
				return false;
			}
		} else if (!contentId.equals(other.contentId)) {
			return false;
		}
		if (page == null) {
			if (other.page != null) {
				return false;
			}
		} else if (!page.equals(other.page)) {
			return false;
		}
		return true;
	}
}
