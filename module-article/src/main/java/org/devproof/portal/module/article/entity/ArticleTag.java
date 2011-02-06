/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.article.entity;

import org.devproof.portal.core.module.tag.entity.AbstractTag;
import org.devproof.portal.module.article.ArticleConstants;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "article_tag")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ArticleConstants.ENTITY_CACHE_REGION)
public class ArticleTag extends AbstractTag<Article> {
    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
    private List<Article> referencedObjects;

    /**
     * @return the referencedObjects
     */
    @Override
    public List<Article> getReferencedObjects() {
        return referencedObjects;
    }

    /**
     * @param referencedObjects the referencedObjects to set
     */
    @Override
    public void setReferencedObjects(List<Article> referencedObjects) {
        this.referencedObjects = referencedObjects;
    }

}
