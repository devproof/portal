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
package org.devproof.portal.module.article.service;

import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.dao.ArticleDao;
import org.devproof.portal.module.article.dao.ArticlePageDao;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticlePageEntity;
import org.devproof.portal.module.article.entity.ArticlePageId;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

/**
 * @author Carsten Hufe
 */
public class ArticleServiceImpl implements ArticleService {
	private ArticleDao articleDao;
	private ArticlePageDao articlePageDao;
	private TagService<ArticleTagEntity> articleTagService;

	@Override
	public boolean existsContentId(String contentId) {
		return articleDao.existsContentId(contentId) > 0;
	}

	@Override
	public List<ArticleEntity> findAllArticlesForRoleOrderedByDateDesc(RoleEntity role, Integer firstResult,
			Integer maxResult) {
		return articleDao.findAllArticlesForRoleOrderedByDateDesc(role, firstResult, maxResult);
	}

	@Override
	public ArticleEntity newArticleEntity() {
		ArticleEntity article = new ArticleEntity();
		article.setAllRights(articleDao.findLastSelectedRights());
		return article;
	}

	@Override
	public ArticlePageEntity newArticlePageEntity(ArticleEntity article, Integer page) {
		return article.newArticlePageEntity(page);
	}

	@Override
	public void delete(ArticleEntity entity) {
		articleDao.delete(entity);
		articleTagService.deleteUnusedTags();
	}

	@Override
	public ArticleEntity findById(Integer id) {
		return articleDao.findById(id);
	}

	@Override
	public void save(ArticleEntity entity) {
		articleDao.save(entity);
		articleTagService.deleteUnusedTags();
	}

	@Override
	public long getPageCount(String contentId) {
		return articlePageDao.getPageCount(contentId);
	}

	@Override
	public ArticlePageEntity findArticlePageByContentIdAndPage(String contentId, Integer page) {
		return articlePageDao.findById(new ArticlePageId(contentId, page));
	}

	@Override
	public ArticleEntity findByContentId(String contentId) {
		return articleDao.findByContentId(contentId);
	}

	@Required
	public void setArticleDao(ArticleDao articleDao) {
		this.articleDao = articleDao;
	}

	@Required
	public void setArticlePageDao(ArticlePageDao articlePageDao) {
		this.articlePageDao = articlePageDao;
	}

	@Required
	public void setArticleTagService(TagService<ArticleTagEntity> articleTagService) {
		this.articleTagService = articleTagService;
	}
}
