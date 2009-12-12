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

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.devproof.portal.core.module.tag.entity.BaseTagEntity;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "article_tag")
final public class ArticleTagEntity extends BaseTagEntity<ArticleEntity> {
	private static final long serialVersionUID = 1L;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "tags")
	private List<ArticleEntity> referencedObjects;

	/**
	 * @return the referencedObjects
	 */
	@Override
	public List<ArticleEntity> getReferencedObjects() {
		return referencedObjects;
	}

	/**
	 * @param referencedObjects
	 *            the referencedObjects to set
	 */
	@Override
	public void setReferencedObjects(final List<ArticleEntity> referencedObjects) {
		this.referencedObjects = referencedObjects;
	}

}
