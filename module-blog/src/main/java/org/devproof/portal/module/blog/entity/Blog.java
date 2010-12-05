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

import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.Modification;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.devproof.portal.module.blog.service.BlogHistorizer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
public class Blog extends BaseBlog {
    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "blog_right_xref", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
    private List<Right> allRights;
    @ManyToMany(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "blog_tag_xref", joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "tagname", referencedColumnName = "tagname"))
    private List<BlogTag> tags;

    // Generated stuff
    public List<Right> getAllRights() {
        if (allRights == null) {
            allRights = new ArrayList<Right>();
        }
        return allRights;
    }

    public void setAllRights(List<Right> allRights) {
        this.allRights = allRights;
    }

    @Transient
    public List<Right> getViewRights() {
        return getRightsStartingWith(getAllRights(), "blog.view");
    }

    @Transient
    public List<Right> getCommentViewRights() {
        return getRightsStartingWith(getAllRights(), "blog.comment.view");
    }

    @Transient
    public List<Right> getCommentWriteRights() {
        return getRightsStartingWith(getAllRights(), "blog.comment.write");
    }

    public List<BlogTag> getTags() {
        if (tags == null) {
            tags = new ArrayList<BlogTag>();
        }
        return tags;
    }

    public void setTags(List<BlogTag> tags) {
        this.tags = tags;
    }
}
