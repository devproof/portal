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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.article.ArticleConstants;
import org.hibernate.annotations.Cascade;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "article", uniqueConstraints = @UniqueConstraint(columnNames = { "content_id" }))
// @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
final public class ArticleEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Integer id;
	@Column(name = "content_id", unique = true)
	private String contentId;
	@Column(name = "title")
	private String title;
	@Lob
	@Column(name = "teaser")
	private String teaser;
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ArticlePageEntity> articlePages;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "article_right_xref", joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
	private List<RightEntity> allRights;
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "article_tag_xref", joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
	private List<ArticleTagEntity> tags;

	@Transient
	public List<RightEntity> getViewRights() {
		return getRightsStartingWith(allRights, "article.view");
	}

	@Transient
	public List<RightEntity> getReadRights() {
		return getRightsStartingWith(allRights, "article.read");
	}

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(final String contentId) {
		this.contentId = contentId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getTeaser() {
		return teaser;
	}

	public void setTeaser(final String teaser) {
		this.teaser = teaser;
	}

	public List<ArticlePageEntity> getArticlePages() {
		if (articlePages == null) {
			articlePages = new ArrayList<ArticlePageEntity>();
		}
		return articlePages;
	}

	public void setArticlePages(final List<ArticlePageEntity> articlePages) {
		this.articlePages = articlePages;
	}

	public List<RightEntity> getAllRights() {
		if (allRights == null) {
			allRights = new ArrayList<RightEntity>();
		}
		return allRights;
	}

	public void setAllRights(final List<RightEntity> allRights) {
		this.allRights = allRights;
	}

	public List<ArticleTagEntity> getTags() {
		return tags;
	}

	public void setTags(final List<ArticleTagEntity> tags) {
		this.tags = tags;
	}

	@Transient
	public String getFullArticle() {
		String back = "";
		if (articlePages != null) {
			final StringBuilder buf = new StringBuilder();
			boolean firstArticlePage = true;
			for (final ArticlePageEntity page : articlePages) {
				if (firstArticlePage) {
					firstArticlePage = false;
				} else {
					buf.append(ArticleConstants.PAGEBREAK);
				}
				buf.append(page.getContent());
			}
			back = buf.toString();
		}
		return back;
	}

	@Transient
	public void setFullArticle(final String fullArticle) {
		String[] splittedPages = getSplittedPages(fullArticle);
		for (int i = 0; i < splittedPages.length; i++) {
			ArticlePageEntity page = null;
			boolean isUpdatablePageAvailable = articlePages != null && articlePages.size() > i;
			if (isUpdatablePageAvailable) {
				page = articlePages.get(i);
			} else {
				page = newArticlePageEntity(i + 1);
				page.setArticle(this);
			}
			page.setContent(splittedPages[i]);
			getArticlePages().add(page);
		}
	}

	@Transient
	public ArticlePageEntity newArticlePageEntity(final Integer page) {
		ArticlePageEntity e = new ArticlePageEntity();
		e.setArticle(this);
		e.setContentId(getContentId());
		e.setPage(page);
		return e;
	}

	private String[] getSplittedPages(final String pages) {
		String splittedPages[] = null;
		if (pages != null) {
			splittedPages = StringUtils.splitByWholeSeparator(pages, ArticleConstants.PAGEBREAK);
		} else {
			splittedPages = new String[1];
			splittedPages[0] = "";
		}
		return splittedPages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final ArticleEntity other = (ArticleEntity) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
