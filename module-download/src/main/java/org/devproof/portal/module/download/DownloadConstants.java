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
package org.devproof.portal.module.download;

import org.apache.wicket.ResourceReference;
import org.devproof.portal.module.download.page.DownloadEditPage;

/**
 * @author Carsten Hufe
 */
public interface DownloadConstants {
    String CONF_DOWNLOADS_PER_PAGE = "downloads_per_page";
    String CONF_DOWNLOAD_VOTE_ENABLED = "download_vote_enabled";
    String CONF_DOWNLOAD_HIDE_BROKEN = "download_hide_broken";
    String CONF_BOX_NUM_LATEST_DOWNLOADS = "box_num_latest_downloads";
    ResourceReference REF_DOWNLOAD_CSS = new ResourceReference(DownloadConstants.class, "css/download.css");
    ResourceReference REF_DOWNLOAD_IMG = new ResourceReference(DownloadConstants.class, "img/download.png");
    String CONF_DOWNLOAD_FEED_TITLE = "download_feed_title";
    String CONF_DOWNLOAD_ENTRIES_IN_FEED = "download_entries_in_feed";
    String ENTITY_CACHE_REGION = "entity.content";

    String QUERY_CACHE_REGION = "query.content";
    String AUTHOR_RIGHT = "page." + DownloadEditPage.class.getSimpleName();
}
