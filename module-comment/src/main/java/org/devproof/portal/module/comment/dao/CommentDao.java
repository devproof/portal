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
package org.devproof.portal.module.comment.dao;

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.dao.GenericDao;
import org.devproof.portal.module.comment.entity.CommentEntity;

/**
 * @author Carsten Hufe
 */
public interface CommentDao extends GenericDao<CommentEntity, Integer> {
	@BulkUpdate("update CommentEntity c set c.accepted = true, c.reviewed = true, c.automaticBlocked = false, c.numberOfBlames = 0 where c = ?")
	public void acceptComment(CommentEntity comment);

	@BulkUpdate("update CommentEntity c set c.accepted = false, c.reviewed = true, c.automaticBlocked = false, c.numberOfBlames = 0 where c = ?")
	public void rejectComment(CommentEntity comment);

	@Query("select count(c) from CommentEntity c where c.moduleName = ? and c.moduleContentId = ?")
	public long findNumberOfComments(String moduleName, String moduleContentId);
}
