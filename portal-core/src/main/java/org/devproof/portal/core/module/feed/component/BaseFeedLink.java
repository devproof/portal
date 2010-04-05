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
package org.devproof.portal.core.module.feed.component;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.page.BaseFeedPage;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

/**
 * @author Carsten Hufe
 */
public abstract class BaseFeedLink extends BookmarkablePageLink<BaseFeedLink> {
	private static final long serialVersionUID = 1L;
	@SpringBean(name = "feedProviderRegistry")
	private FeedProviderRegistry feedProviderRegistry;
	private Class<? extends TemplatePage> page;
	private boolean hasFeedSupport;
	
	public BaseFeedLink(String id, Class<? extends TemplatePage> page, Class<? extends BaseFeedPage> feedPage) {
		super(id, feedPage);
		this.page = page;
		setHasFeedSupport();
		setVisiblity();
		setLinkParameter(); 
		add(createTitleAttributeModifier());
		add(createTypeAttributeModifier());
		add(createRelAttributeModifier());
	}

	private void setLinkParameter() {
		if (hasFeedSupport) {
			String path = getPagePath();
			setParameter("0", path);
		}
	}

	private SimpleAttributeModifier createTitleAttributeModifier() {
		String title = getFeedPageTitle();
		String feedName = new StringResourceModel("feedName", this, null, new String[] { title }).getString();
		SimpleAttributeModifier titleModifier = new SimpleAttributeModifier("title", feedName);
		return titleModifier;
	}

	private String getFeedPageTitle() {
		if(hasFeedSupport) {
			String path = getPagePath();
			return feedProviderRegistry.getFeedProviderByPath(path).getFeedName();
		}
		return "";
	}

	private String getPagePath() {
		return feedProviderRegistry.getPathByPageClass(page);
	}

	private void setVisiblity() {
		setVisible(hasFeedSupport);
	}

	private void setHasFeedSupport() {
		hasFeedSupport = feedProviderRegistry.hasFeedSupport(page);
	}

	private SimpleAttributeModifier createTypeAttributeModifier() {
		return new SimpleAttributeModifier("type", getContentType());
	}

	private SimpleAttributeModifier createRelAttributeModifier() {
		return new SimpleAttributeModifier("rel", "alternate");
	}
	
	protected abstract String getContentType();
}
