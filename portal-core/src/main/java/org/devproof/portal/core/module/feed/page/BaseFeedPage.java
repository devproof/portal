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

import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * @author Carsten Hufe
 */
public abstract class BaseFeedPage extends WebPage {
	@SpringBean(name = "feedProviderRegistry")
	private FeedProviderRegistry feedProviderRegistry;
	private String path = "";

	public BaseFeedPage(final PageParameters params) {
		super(params);
		if (params.size() > 0) {
			this.path = params.getString("0");
		}
	}

	@Override
	protected final void onRender(final MarkupStream markupStream) {
		getResponse().setContentType(getContentType());
		PrintWriter writer = new PrintWriter(getResponse().getOutputStream());
		SyndFeedOutput output = new SyndFeedOutput();
		try {
			FeedProvider feedProvider = this.feedProviderRegistry.getFeedProviderByPath(this.path);
			SyndFeed feed = null;
			if (feedProvider != null) {
				feed = feedProvider.getFeed();
			} else {
				feed = new SyndFeedImpl();
			}
			feed.setFeedType(getFeedType());
			output.output(feed, writer);
			writer.close();
		} catch (IOException e) {
			throw new UnhandledException("Error streaming feed.", e);
		} catch (FeedException e) {
			throw new UnhandledException("Error streaming feed.", e);
		}
	}

	protected abstract String getContentType();

	protected abstract String getFeedType();

	@Override
	public String getMarkupType() {
		return "xml";
	}
}
