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

import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

/**
 * @author Carsten Hufe
 */
public class FeedOverviewPage extends TemplatePage {
	@SpringBean(name = "feedProviderRegistry")
	private FeedProviderRegistry feedProviderRegistry;

	public FeedOverviewPage(final PageParameters params) {
		super(params);
		RepeatingView tableRow = new RepeatingView("tableRow");
		add(tableRow);
		Map<String, FeedProvider> allFeedProvider = feedProviderRegistry.getAllFeedProvider();
		for (String path : allFeedProvider.keySet()) {
			FeedProvider provider = allFeedProvider.get(path);
			WebMarkupContainer row = new WebMarkupContainer(tableRow.newChildId());
			row.add(new Label("path", path));
			row.add(new Label("pages", getSupportedPagesString(provider)).setEscapeModelStrings(false));
			row.add(new BookmarkablePageLink<Atom1FeedPage>("atom1Link", Atom1FeedPage.class, new PageParameters("0="
					+ path)));
			row.add(new BookmarkablePageLink<Rss2FeedPage>("rss2Link", Rss2FeedPage.class, new PageParameters("0="
					+ path)));
			tableRow.add(row);
		}
	}

	private String getSupportedPagesString(final FeedProvider provider) {
		StringBuilder buf = new StringBuilder();
		for (Class<? extends TemplatePage> page : provider.getSupportedFeedPages()) {
			buf.append(page.getName()).append("<br/>");
		}
		return buf.toString();
	}
}
