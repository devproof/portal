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
package org.devproof.portal.core.module.feed.component;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.page.Rss2FeedPage;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

/**
 * @author Carsten Hufe
 */
public class Rss2Link extends BookmarkablePageLink<Rss2Link> {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "feedProviderRegistry")
	private FeedProviderRegistry feedProviderRegistry;

	public Rss2Link(String id, Class<? extends TemplatePage> page) {
		super(id, Rss2FeedPage.class);
		String title = "";
		if (feedProviderRegistry.hasFeedSupport(page)) {
			String path = feedProviderRegistry.getPathByPageClass(page);
			setParameter("0", path);
			title = feedProviderRegistry.getFeedProviderByPath(path).getFeedName();
		} else {
			setVisible(false);
		}
		add(new SimpleAttributeModifier("title",
				new StringResourceModel("feedName", this, null, new String[] { title }).getString()));
		add(new SimpleAttributeModifier("type", "application/rss+xml"));
		add(new SimpleAttributeModifier("rel", "alternate"));
	}
}
