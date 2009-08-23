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
package org.devproof.portal.module.blog.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.module.blog.page.BlogPage;
import org.springframework.beans.factory.annotation.Required;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * @author Carsten Hufe
 */
public class BlogFeedProvider implements FeedProvider {
	private BlogService blogService;

	@Override
	public SyndFeed getFeed() {
		SyndFeed feed = new SyndFeedImpl();
		feed.setTitle("Sample Feed"); // new conf parameter
		feed.setLink("http://mysite.com"); // conf parameter?
		feed.setDescription("Sample Feed for how cool Wicket is");

		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		SyndEntry entry;
		SyndContent description;

		entry = new SyndEntryImpl();
		entry.setTitle("Article One");
		entry.setLink("http://mysite.com/article/one");
		entry.setPublishedDate(new Date());
		description = new SyndContentImpl();
		description.setType("text/plain");
		description.setValue("Article describing how cool wicket is.");
		entry.setDescription(description);
		entries.add(entry);
		feed.setEntries(entries);
		return feed;
	}

	@Override
	public List<Class<? extends TemplatePage>> getSupportedFeedPages() {
		List<Class<? extends TemplatePage>> pages = new ArrayList<Class<? extends TemplatePage>>();
		pages.add(BlogPage.class);
		return pages;
	}

	@Required
	public void setBlogService(final BlogService blogService) {
		this.blogService = blogService;
	}
}
