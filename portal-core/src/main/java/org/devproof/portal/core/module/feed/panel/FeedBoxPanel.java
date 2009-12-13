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
package org.devproof.portal.core.module.feed.panel;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.feed.FeedConstants;
import org.devproof.portal.core.module.feed.page.Atom1FeedPage;
import org.devproof.portal.core.module.feed.page.Rss2FeedPage;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

/**
 * @author Carsten Hufe
 */
public class FeedBoxPanel extends Panel implements BoxTitleVisibility {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer titleContainer;

	@SpringBean(name = "feedProviderRegistry")
	private FeedProviderRegistry feedProviderRegistry;

	public FeedBoxPanel(String id, Class<? extends Page> page) {
		super(id);
		add(titleContainer = new WebMarkupContainer("title"));
		String pathByPageClass = feedProviderRegistry.getPathByPageClass(page);
		PageParameters pageParameters = new PageParameters("0=" + pathByPageClass);
		BookmarkablePageLink<Atom1FeedPage> atom1FeedLink = new BookmarkablePageLink<Atom1FeedPage>("atomLink",
				Atom1FeedPage.class, pageParameters);
		BookmarkablePageLink<Rss2FeedPage> rss2FeedLink = new BookmarkablePageLink<Rss2FeedPage>("rssLink",
				Rss2FeedPage.class, pageParameters);
		atom1FeedLink.add(new Image("atomImage", FeedConstants.REF_ATOM1));
		rss2FeedLink.add(new Image("rssImage", FeedConstants.REF_RSS2));
		add(atom1FeedLink);
		add(rss2FeedLink);
		setVisible(feedProviderRegistry.hasFeedSupport(page));
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
