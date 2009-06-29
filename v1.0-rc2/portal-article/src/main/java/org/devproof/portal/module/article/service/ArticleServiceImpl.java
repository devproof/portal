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
package org.devproof.portal.module.article.service;

import java.util.List;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.dao.ArticleDao;
import org.devproof.portal.module.article.dao.ArticlePageDao;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticlePageId;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class ArticleServiceImpl implements ArticleService {
	private ArticleDao articleDao;
	private ArticlePageDao articlePageDao;
	private TagService<ArticleTagEntity> articleTagService;

	@Override
	public boolean existsContentId(final String contentId) {
		return this.articleDao.existsContentId(contentId) > 0;
	}

	@Override
	public List<ArticleEntity> findAllArticlesForRoleOrderedByDateDesc(final RoleEntity role, final Integer firstResult, final Integer maxResult) {
		return this.articleDao.findAllArticlesForRoleOrderedByDateDesc(role, firstResult, maxResult);
	}

	@Override
	public ArticleEntity newArticleEntity() {
		return new ArticleEntity();
	}

	@Override
	public ArticlePageEntity newArticlePageEntity(final ArticleEntity article, final Integer page) {
		final ArticlePageEntity e = new ArticlePageEntity();
		e.setArticle(article);
		e.setContentId(article.getContentId());
		e.setPage(page);
		return e;
	}

	@Override
	public void delete(final ArticleEntity entity) {
		this.articleDao.delete(entity);
		this.articleTagService.deleteUnusedTags();
	}

	@Override
	public List<ArticleEntity> findAll() {
		return this.articleDao.findAll();
	}

	@Override
	public ArticleEntity findById(final Integer id) {
		return this.articleDao.findById(id);
	}

	@Override
	public void save(final ArticleEntity entity) {
		this.articleDao.save(entity);
		this.articleTagService.deleteUnusedTags();
	}

	@Override
	public long getPageCount(final String contentId) {
		return this.articlePageDao.getPageCount(contentId);
	}

	@Override
	public ArticlePageEntity findArticlePageByContentIdAndPage(final String contentId, final Integer page) {
		return this.articlePageDao.findById(new ArticlePageId(contentId, page));
	}

	@Required
	public void setArticleDao(final ArticleDao articleDao) {
		this.articleDao = articleDao;
	}

	@Required
	public void setArticlePageDao(final ArticlePageDao articlePageDao) {
		this.articlePageDao = articlePageDao;
	}

	@Required
	public void setArticleTagService(final TagService<ArticleTagEntity> articleTagService) {
		this.articleTagService = articleTagService;
	}
}
