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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author Carsten Hufe
 */
public class AtomFeedPage extends WebPage {

	public AtomFeedPage(final PageParameters params) {
		super(params);
	}

	@Override
	protected final void onRender(final MarkupStream markupStream) {
		// description.setType("application/atom+xml");
		// description.setType("application/rss+xml");
		getResponse().setContentType("application/atom+xml");
		PrintWriter writer = new PrintWriter(getResponse().getOutputStream());
		SyndFeedOutput output = new SyndFeedOutput();
		try {
			output.output(getFeed(), writer);
			writer.close();
		} catch (IOException e) {
			throw new UnhandledException("Error streaming feed.", e);
		} catch (FeedException e) {
			throw new UnhandledException("Error streaming feed.", e);
		}
	}

	protected SyndFeed getFeed() {
		SyndFeed feed = new SyndFeedImpl();
		feed.setFeedType("atom_1.0");
		// rss_2.0
		feed.setTitle("Sample Feed");
		feed.setLink("http://mysite.com");
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
		description.setValue("Article descriping how cool wicket is.");
		entry.setDescription(description);
		entries.add(entry);

		feed.setEntries(entries);

		return feed;
	}

	@Override
	public String getMarkupType() {
		return "xml";
	}
}
