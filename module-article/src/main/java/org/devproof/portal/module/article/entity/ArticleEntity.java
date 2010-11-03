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
import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.common.model.EntityId;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "article", uniqueConstraints = @UniqueConstraint(columnNames = {"content_id"}))
@CacheQuery(region = ArticleConstants.QUERY_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ArticleConstants.ENTITY_CACHE_REGION)
@RegisterGenericDataProvider(value = "articleDataProvider", sortProperty = "title", sortAscending = true, queryClass = ArticleQuery.class)
public class ArticleEntity extends BaseEntity implements EntityId {
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
    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticlePageEntity> articlePages;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "article_right_xref", joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
    @Fetch(FetchMode.SUBSELECT)
    private List<RightEntity> allRights;
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "article_tag_xref", joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
    private List<ArticleTagEntity> tags;

    @Transient
    public List<RightEntity> getCommentViewRights() {
        return getRightsStartingWith(allRights, "article.comment.view");
    }

    @Transient
    public List<RightEntity> getCommentWriteRights() {
        return getRightsStartingWith(allRights, "article.comment.write");
    }

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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public List<ArticlePageEntity> getArticlePages() {
        if (articlePages == null) {
            articlePages = new ArrayList<ArticlePageEntity>();
        }
        return articlePages;
    }

    public void setArticlePages(List<ArticlePageEntity> articlePages) {
        this.articlePages = articlePages;
    }

    public List<RightEntity> getAllRights() {
        if (allRights == null) {
            allRights = new ArrayList<RightEntity>();
        }
        return allRights;
    }

    public void setAllRights(List<RightEntity> allRights) {
        this.allRights = allRights;
    }

    public List<ArticleTagEntity> getTags() {
        return tags;
    }

    public void setTags(List<ArticleTagEntity> tags) {
        this.tags = tags;
    }

    @Transient
    public String getFullArticle() {
        String back = "";
        if (articlePages != null) {
            StringBuilder buf = new StringBuilder();
            boolean firstArticlePage = true;
            for (ArticlePageEntity page : articlePages) {
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
    public void setFullArticle(String fullArticle) {
        if (fullArticle == null) {
            fullArticle = " ";
        }
        List<ArticlePageEntity> pages = getArticlePages();
        List<String> splittedPages = getSplittedPages(fullArticle);
        for (int i = 0; i < splittedPages.size(); i++) {
            final ArticlePageEntity page;
            boolean isUpdatablePageAvailable = articlePages != null && articlePages.size() > i;
            if (isUpdatablePageAvailable) {
                page = articlePages.get(i);
            } else {
                page = newArticlePageEntity(i + 1);
                page.setArticle(this);
                pages.add(page);
            }
            page.setContent(splittedPages.get(i));
            // pages.add(page);
        }
        for (int i = pages.size() - 1; i >= splittedPages.size(); i--) {
            pages.remove(i);
        }
    }

    @Transient
    public ArticlePageEntity newArticlePageEntity(Integer page) {
        ArticlePageEntity e = new ArticlePageEntity();
        e.setArticle(this);
        e.setContentId(getContentId());
        e.setPage(page);
        return e;
    }

    private List<String> getSplittedPages(String pages) {
        String[] splitted = StringUtils.splitByWholeSeparator(pages, "page-break-after");
        List<String> result = new ArrayList<String>();
        if (splitted.length > 1) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < splitted.length; i++) {
                String actual = splitted[i];
                int open = actual.lastIndexOf('<');
                int close = actual.lastIndexOf('>');
                if (open < 0 || close > open) {
                    // kein tag
                    buf.append(actual);
                    if (splitted.length - 1 != i) buf.append("page-break-after");
                } else {
                    // tag
                    buf.append(StringUtils.substringBeforeLast(actual, "<"));
                    result.add(buf.toString());
                    buf = new StringBuilder();
                    String closeTag = StringUtils.substringAfterLast(actual, "<");
                    closeTag = "</" + StringUtils.substringBefore(closeTag, " ") + ">";
                    splitted[i + 1] = StringUtils.substringAfter(splitted[i + 1], closeTag);
                }
            }
            if (buf.length() > 0) {
                result.add(buf.toString());
            }
        } else {
            result.add(pages);
        }
        return result;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        ArticleEntity other = (ArticleEntity) obj;
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
