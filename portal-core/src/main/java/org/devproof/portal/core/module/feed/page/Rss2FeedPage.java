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
package org.devproof.portal.core.module.feed.page;

import org.apache.wicket.PageParameters;

/**
 * RSS 2.0 Feed
 * 
 * @author Carsten Hufe
 */
public class Rss2FeedPage extends BaseFeedPage {

	public Rss2FeedPage(PageParameters params) {
		super(params);
	}

	@Override
	protected String getContentType() {
		return "application/rss+xml";
	}

	@Override
	protected String getFeedType() {
		return "rss_2.0";
	}
}
