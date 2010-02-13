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
package org.devproof.portal.module.download;

import org.apache.wicket.ResourceReference;

/**
 * @author Carsten Hufe
 */
public class DownloadConstants {
	private DownloadConstants() {
	}

	public static final String CONF_DOWNLOADS_PER_PAGE = "downloads_per_page";
	public static final String CONF_DOWNLOAD_VOTE_ENABLED = "download_vote_enabled";
	public static final String CONF_DOWNLOAD_HIDE_BROKEN = "download_hide_broken";
	public static final String CONF_BOX_NUM_LATEST_DOWNLOADS = "box_num_latest_downloads";
	public static final ResourceReference REF_DOWNLOAD_CSS = new ResourceReference(DownloadConstants.class,
			"css/download.css");
	public static final ResourceReference REF_DOWNLOAD_IMG = new ResourceReference(DownloadConstants.class,
			"img/download.png");
	public static final String CONF_DOWNLOAD_FEED_TITLE = "download_feed_title";
	public static final String CONF_DOWNLOAD_ENTRIES_IN_FEED = "download_entries_in_feed";

	public static final String ENTITY_CACHE_REGION = "entity.content";
	public static final String QUERY_CACHE_REGION = "query.content";
}
