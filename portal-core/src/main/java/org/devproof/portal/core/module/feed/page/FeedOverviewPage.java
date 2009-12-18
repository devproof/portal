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
package org.devproof.portal.core.module.feed.page;

import java.util.Map;

import org.apache.wicket.Component;
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
	private Map<String, FeedProvider> allFeedProvider;
	
	public FeedOverviewPage(PageParameters params) {
		super(params);
		setAllFeedProvider();
		add(createFeedOverviewTable());
	}

	private void setAllFeedProvider() {
		allFeedProvider = feedProviderRegistry.getAllFeedProvider();
	}

	private RepeatingView createFeedOverviewTable() {
		RepeatingView tableRow = new RepeatingView("tableRow");
		for (String path : allFeedProvider.keySet()) {
			WebMarkupContainer row = createFeedRow(tableRow.newChildId(), path);
			tableRow.add(row);
		}
		return tableRow;
	}

	private WebMarkupContainer createFeedRow(String id, String path) {
		FeedProvider provider = allFeedProvider.get(path);
		WebMarkupContainer row = new WebMarkupContainer(id);
		row.add(createFeedNameLabel(provider));
		row.add(createPathLabel(path));
		row.add(createSupportedPagesLabel(provider));
		row.add(createAtom1Link(path));
		row.add(createRss2Link(path));
		return row;
	}

	private BookmarkablePageLink<Rss2FeedPage> createRss2Link(String path) {
		return new BookmarkablePageLink<Rss2FeedPage>("rss2Link", Rss2FeedPage.class, new PageParameters("0="
				+ path));
	}

	private BookmarkablePageLink<Atom1FeedPage> createAtom1Link(String path) {
		return new BookmarkablePageLink<Atom1FeedPage>("atom1Link", Atom1FeedPage.class, new PageParameters("0="
				+ path));
	}

	private Component createSupportedPagesLabel(FeedProvider provider) {
		Label supportedPages = new Label("pages", getSupportedPagesString(provider));
		supportedPages.setEscapeModelStrings(false);
		return supportedPages;
	}

	private Label createPathLabel(String path) {
		return new Label("path", path);
	}

	private Label createFeedNameLabel(FeedProvider provider) {
		return new Label("feedName", provider.getFeedName());
	}

	private String getSupportedPagesString(FeedProvider provider) {
		StringBuilder buf = new StringBuilder();
		for (Class<? extends TemplatePage> page : provider.getSupportedFeedPages()) {
			buf.append(page.getSimpleName()).append(", ");
		}
		return buf.toString();
	}
}
