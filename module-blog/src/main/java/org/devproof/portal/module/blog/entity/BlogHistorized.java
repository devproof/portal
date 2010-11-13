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
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.query.BlogQuery;
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
@Table(name = "blog_historized")
@RegisterGenericDataProvider(value = "blogHistorizedDataProvider", sortProperty = "modifiedAt", sortAscending = false)
public class BlogHistorized extends BaseBlog {
    private static final long serialVersionUID = 1L;

    @Lob
    @Column(name = "rights")
    private String rights;
    @Lob
    @Column(name = "tags")
    private String tags;

    // Generated stuff
    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
