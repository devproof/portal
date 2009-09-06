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
package org.devproof.portal.module.bookmark;

import org.apache.wicket.ResourceReference;

/**
 * @author Carsten Hufe
 */
public class BookmarkConstants {
	private BookmarkConstants() {
	}

	public static final String CONF_BOOKMARKS_PER_PAGE = "bookmarks_per_page";
	public static final String CONF_BOOKMARK_VOTE_ENABLED = "bookmark_vote_enabled";
	public static final String CONF_BOOKMARK_HIDE_BROKEN = "bookmark_hide_broken";
	public static final String USER_AGENT = "devproofPortal";
	public static final String DELICIOUS_API = "https://api.del.icio.us/v1/posts/all?";
	public static final String CONF_BOX_NUM_LATEST_BOOKMARKS = "box_num_latest_bookmarks";
	public static final ResourceReference REF_DELICIOUS = new ResourceReference(BookmarkConstants.class,
			"img/delicious.gif");
	public static final ResourceReference REF_BOOKMARK_CSS = new ResourceReference(BookmarkConstants.class,
			"css/bookmark.css");
	public static final String CONF_BOOKMARK_ENTRIES_IN_FEED = "bookmark_entries_in_feed";
	public static final String CONF_BOOKMARK_FEED_TITLE = "bookmark_feed_title";
}
