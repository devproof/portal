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
package org.devproof.portal.module.bookmark;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.module.bookmark.page.BookmarkEditPage;

/**
 * @author Carsten Hufe
 */
public interface BookmarkConstants {
	String CONF_BOOKMARKS_PER_PAGE = "bookmarks_per_page";
	String CONF_BOOKMARK_VOTE_ENABLED = "bookmark_vote_enabled";
	String CONF_BOOKMARK_HIDE_BROKEN = "bookmark_hide_broken";
	String USER_AGENT = "devproofPortal";
	String DELICIOUS_API = "https://api.del.icio.us/v1/posts/all?";
	String CONF_BOX_NUM_LATEST_BOOKMARKS = "box_num_latest_bookmarks";
	ResourceReference REF_DELICIOUS_IMG = new ResourceReference(BookmarkConstants.class, "img/delicious.gif");
	ResourceReference REF_LINK_IMG = new ResourceReference(BookmarkConstants.class, "img/link.png");
	ResourceReference REF_BOOKMARK_CSS = new ResourceReference(BookmarkConstants.class, "css/bookmark.css");
	String CONF_BOOKMARK_ENTRIES_IN_FEED = "bookmark_entries_in_feed";
	String CONF_BOOKMARK_FEED_TITLE = "bookmark_feed_title";

	String ENTITY_CACHE_REGION = "entity.content";
	String QUERY_CACHE_REGION = "query.content";
	String AUTHOR_RIGHT = "page." + BookmarkEditPage.class.getSimpleName();
}
