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
package org.devproof.portal.core.module.feed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.role.entity.RoleEntity;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * @author Carsten Hufe
 */
public class DummyFeedProviderImpl implements FeedProvider, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public SyndFeed getFeed(RequestCycle rc, RoleEntity role) {
		SyndFeed feed = new SyndFeedImpl();
		feed.setTitle(getFeedName());
		feed.setLink("http://dummy.feed.link");
		feed.setDescription("dummy feed description");

		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		SyndEntry entry;
		SyndContent description;

		entry = new SyndEntryImpl();
		entry.setTitle("dummy title");
		entry.setLink("http://dummy.url");
		entry.setPublishedDate(new Date());
		description = new SyndContentImpl();
		description.setType("text/plain");
		description.setValue("dummy value");
		entry.setDescription(description);
		entries.add(entry);
		feed.setEntries(entries);
		return feed;
	}

	@Override
	public String getFeedName() {
		return "dummy feed";
	}

	@Override
	public List<Class<? extends TemplatePage>> getSupportedFeedPages() {
		List<Class<? extends TemplatePage>> pages = new ArrayList<Class<? extends TemplatePage>>();
		pages.add(DummyPage.class);
		return pages;
	}

}
