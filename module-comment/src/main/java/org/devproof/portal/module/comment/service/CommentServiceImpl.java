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
package org.devproof.portal.module.comment.service;

import java.util.List;

import org.devproof.portal.module.comment.dao.CommentDao;
import org.devproof.portal.module.comment.entity.CommentEntity;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author Carsten Hufe
 */
public class CommentServiceImpl implements CommentService {
	private CommentDao commentDao;

	@Override
	public CommentEntity newCommentEntity() {
		return new CommentEntity();
	}

	@Override
	public void delete(CommentEntity entity) {
		commentDao.save(entity);
	}

	@Override
	public List<CommentEntity> findAll() {
		return commentDao.findAll();
	}

	@Override
	public CommentEntity findById(Integer id) {
		return commentDao.findById(id);
	}

	@Override
	public void save(CommentEntity entity) {
		commentDao.save(entity);
	}

	@Override
	public void rejectComment(CommentEntity comment) {
		commentDao.rejectComment(comment);
	}

	@Override
	public void acceptComment(CommentEntity comment) {
		commentDao.acceptComment(comment);
	}

	@Required
	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}
}
