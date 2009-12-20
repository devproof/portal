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
package org.devproof.portal.module.article.panel;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.page.ArticlePage;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * Latest article box
 * 
 * @author Carsten Hufe
 */
public class ArticleBoxPanel extends Panel implements BoxTitleVisibility {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private WebMarkupContainer titleContainer;
	private List<ArticleEntity> latestArticles;
	
	public ArticleBoxPanel(String id) {
		super(id);
		setLatestArticles();
		setVisibility();
		add(createTitleContainer());
		add(createRepeatingViewWithArticles());
	}

	private Component setVisibility() {
		return setVisible(isArticleAvailable());
	}

	private boolean isArticleAvailable() {
		return latestArticles.size() > 0;
	}

	private List<ArticleEntity> setLatestArticles() {
		latestArticles = getLatestArticles();
		return latestArticles;
	}

	private WebMarkupContainer createTitleContainer() {
		titleContainer = new WebMarkupContainer("title");
		return titleContainer;
	}

	private List<ArticleEntity> getLatestArticles() {
		Integer numberOfLatestArticles = configurationService
				.findAsInteger(ArticleConstants.CONF_BOX_NUM_LATEST_ARTICLES);
		PortalSession session = (PortalSession) getSession();
		return articleService.findAllArticlesForRoleOrderedByDateDesc(session.getRole(),
				0, numberOfLatestArticles);
	}

	private RepeatingView createRepeatingViewWithArticles() {
		RepeatingView repeating = new RepeatingView("repeating");
		for (ArticleEntity article : latestArticles) {
			WebMarkupContainer item = new WebMarkupContainer(repeating.newChildId());
			repeating.add(item);
			item.add(createLinkToArticle(article));
		}
		return repeating;
	}

	private BookmarkablePageLink<ArticlePage> createLinkToArticle(ArticleEntity article) {
		BookmarkablePageLink<ArticlePage> link = new BookmarkablePageLink<ArticlePage>("link", ArticlePage.class);
		link.setParameter("id", article.getId());
		link.add(new Label("linkName", article.getTitle()));
		return link;
	}

	@Override
	public void setTitleVisible(boolean visible) {
		titleContainer.setVisible(visible);
	}
}
