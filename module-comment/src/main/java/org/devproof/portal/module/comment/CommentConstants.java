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
package org.devproof.portal.module.comment;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.module.comment.page.CommentAdminPage;

/**
 * @author Carsten Hufe
 */
public interface CommentConstants {
	String CONF_SHOW_REAL_AUTHOR = "comment_show_real_author";

	ResourceReference REF_COMMENTS_ADD_IMG = new ResourceReference(CommentConstants.class, "img/comments_add.png");
	ResourceReference REF_COMMENTS_IMG = new ResourceReference(CommentConstants.class, "img/comments.png");
	ResourceReference REF_ACCEPT_IMG = new ResourceReference(CommentConstants.class, "img/accept.png");
	ResourceReference REF_REJECT_IMG = new ResourceReference(CommentConstants.class, "img/deny.png");
	String CONF_COMMENT_BLAMED_THRESHOLD = "comment_blamed_threshold";
	String CONF_COMMENT_SHOW_REAL_AUTHOR = "comment_show_real_author";
	String CONF_COMMENT_NUMBER_PER_PAGE = "comment_number_per_page";
	String CONF_COMMENT_NUMBER_PER_PAGE_ADMIN = "comment_number_per_page_admin";
	String CONF_COMMENT_SHOW_ONLY_REVIEWED = "comment_show_only_reviewed";
	String CONF_NOTIFY_NEW_COMMENT = "spring.emailTemplateDao.findAll.subject.id.newcommentnotification";
	String CONF_NOTIFY_AUTOBLOCKED = "spring.emailTemplateDao.findAll.subject.id.autoblockednotification";
	String CONF_NOTIFY_VIOLATION = "spring.emailTemplateDao.findAll.subject.id.violationnotification";
	String ENTITY_CACHE_REGION = "entity.usercontent";
	String QUERY_CACHE_REGION = "query.usercontent";
	String AUTHOR_RIGHT = "page." + CommentAdminPage.class.getSimpleName();
}
