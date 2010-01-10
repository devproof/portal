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

/**
 * @author Carsten Hufe
 */
public class CommentConstants {
	private CommentConstants() {
	}

	public static final String CONF_SHOW_REAL_AUTHOR = "comment_show_real_author";

	public static final ResourceReference REF_COMMENTS_ADD_IMG = new ResourceReference(CommentConstants.class,
			"img/comments_add.png");
	public static final ResourceReference REF_COMMENTS_IMG = new ResourceReference(CommentConstants.class,
			"img/comments.png");
	public static final ResourceReference REF_ACCEPT_IMG = new ResourceReference(CommentConstants.class,
			"img/accept.png");
	public static final ResourceReference REF_REJECT_IMG = new ResourceReference(CommentConstants.class, "img/deny.png");
}
