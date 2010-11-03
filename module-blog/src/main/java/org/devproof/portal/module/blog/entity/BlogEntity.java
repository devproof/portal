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
package org.devproof.portal.module.blog.entity;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.BaseEntity;
import org.devproof.portal.core.module.right.entity.RightEntity;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Represents a blog entry
 *
 * @author Carsten Hufe
 */
@Entity
@Table(name = "blog")
@CacheQuery(region = BlogConstants.QUERY_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = BlogConstants.ENTITY_CACHE_REGION)
@RegisterGenericDataProvider(value = "blogDataProvider", sortProperty = "createdAt", sortAscending = false, queryClass = BlogQuery.class)
public class BlogEntity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "headline")
    private String headline;
    @Lob
    @Column(name = "content")
    private String content;
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "blog_right_xref", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
    private List<RightEntity> allRights;
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "blog_tag_xref", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
    private List<BlogTagEntity> tags;

    // Generated stuff

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    @Transient
    public List<RightEntity> getViewRights() {
        return getRightsStartingWith(allRights, "blog.view");
    }

    @Transient
    public List<RightEntity> getCommentViewRights() {
        return getRightsStartingWith(allRights, "blog.comment.view");
    }

    @Transient
    public List<RightEntity> getCommentWriteRights() {
        return getRightsStartingWith(allRights, "blog.comment.write");
    }

    public List<BlogTagEntity> getTags() {
        if (tags == null) {
            tags = new ArrayList<BlogTagEntity>();
        }
        return tags;
    }

    public void setTags(List<BlogTagEntity> tags) {
        this.tags = tags;
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
        BlogEntity other = (BlogEntity) obj;
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
