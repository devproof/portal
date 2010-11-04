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
package org.devproof.portal.module.comment.repository;

import org.devproof.portal.core.module.common.annotation.BulkUpdate;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.module.comment.CommentConstants;
import org.devproof.portal.module.comment.entity.Comment;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@org.devproof.portal.core.config.GenericRepository("commentRepository")
@CacheQuery(region = CommentConstants.QUERY_CACHE_REGION)
public interface CommentRepository extends CrudRepository<Comment, Integer> {
    @BulkUpdate("update Comment c set c.accepted = true, c.reviewed = true, c.automaticBlocked = false, c.numberOfBlames = 0 where c = ?")
    void acceptComment(Comment comment);

    @BulkUpdate("update Comment c set c.accepted = false, c.reviewed = true, c.automaticBlocked = false, c.numberOfBlames = 0 where c = ?")
    void rejectComment(Comment comment);

    @Query("select count(c) from Comment c where c.moduleName = ? and c.moduleContentId = ? and c.accepted = true and c.reviewed = true and c.automaticBlocked = false")
    long findNumberOfReviewedComments(String moduleName, String moduleContentId);

    @Query("select count(c) from Comment c where c.moduleName = ? and c.moduleContentId = ? and c.automaticBlocked = false and ((c.accepted = true and c.reviewed = true) or c.reviewed = false)")
    long findNumberOfComments(String moduleName, String moduleContentId);

    @Query("select distinct c.moduleName from Comment c")
    List<String> findAllModuleNames();
}
