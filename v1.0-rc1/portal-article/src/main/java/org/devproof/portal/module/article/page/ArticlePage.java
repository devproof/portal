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
package org.devproof.portal.module.article.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.component.ExternalImage;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.tag.panel.ContentTagPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.devproof.portal.module.article.panel.ArticleSearchBoxPanel;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * @author Carsten Hufe
 */
public class ArticlePage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "articleDataProvider")
	private QueryDataProvider<ArticleEntity> articleDataProvider;
	@SpringBean(name = "articleTagService")
	private TagService<ArticleTagEntity> articleTagService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;
	private final ArticleDataView dataView;

	public ArticlePage(final PageParameters params) {
		super(params);
		addSyntaxHighlighter();

		final PortalSession session = (PortalSession) getSession();
		final ArticleQuery query = new ArticleQuery();
		if (!session.hasRight("article.view")) {
			query.setRole(session.getRole());
		}

		this.articleDataProvider.setQueryObject(query);

		this.dataView = new ArticleDataView("listArticle", params);
		addFilterBox(new ArticleSearchBoxPanel("box", query, this.articleDataProvider, this, this.dataView, params));

		this.add(this.dataView);
		this.add(new BookmarkablePagingPanel("paging", this.dataView, ArticlePage.class, params));
		this.addTagCloudBox(this.articleTagService, new PropertyModel<ArticleTagEntity>(query, "tag"), ArticlePage.class, params);
	}

	private class ArticleDataView extends DataView<ArticleEntity> {
		private static final long serialVersionUID = 1L;
		private final boolean onlyOne;
		private final PageParameters params;

		public ArticleDataView(final String id, final PageParameters params) {
			super(id, ArticlePage.this.articleDataProvider);
			this.params = params;
			this.onlyOne = ArticlePage.this.articleDataProvider.size() == 1;
			setItemsPerPage(ArticlePage.this.configurationService.findAsInteger(ArticleConstants.CONF_ARTICLES_PER_PAGE));
		}

		@Override
		protected void populateItem(final Item<ArticleEntity> item) {
			final ArticleEntity article = item.getModelObject();
			item.setOutputMarkupId(true);
			if (this.onlyOne) {
				setPageTitle(article.getTitle());
			}
			final ArticleView articleViewPanel = new ArticleView("articleView", article, this.params);
			if (isAuthor()) {
				articleViewPanel.addOrReplace(new AuthorPanel<ArticleEntity>("authorButtons", article) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onDelete(final AjaxRequestTarget target) {
						ArticlePage.this.articleService.delete(getEntity());
						item.setVisible(false);
						target.addComponent(item);
						target.addComponent(getFeedback());
						info(this.getString("msg.deleted"));
					}

					@Override
					public void onEdit(final AjaxRequestTarget target) {
						// Reload because LazyIntialization occur
						final ArticleEntity tmp = ArticlePage.this.articleService.findById(article.getId());
						this.setResponsePage(new ArticleEditPage(tmp));
					}
				});
			}
			item.add(articleViewPanel);
		}
	}

	/**
	 * Shows an article teaser
	 */
	private class ArticleView extends Fragment {

		private static final long serialVersionUID = 1L;

		public ArticleView(final String id, final ArticleEntity articleEntity, final PageParameters params) {
			super(id, "articleView", ArticlePage.this);
			final PortalSession session = (PortalSession) getSession();
			final boolean allowedToRead = session.hasRight("article.read") || session.hasRight(articleEntity.getReadRights());
			this.add(new WebMarkupContainer("authorButtons"));
			final BookmarkablePageLink<ArticleViewPage> titleLink = new BookmarkablePageLink<ArticleViewPage>("titleLink", ArticleViewPage.class);
			titleLink.setParameter("0", articleEntity.getContentId());
			titleLink.setEnabled(allowedToRead);
			titleLink.add(new Label("titleLabel", articleEntity.getTitle()));
			this.add(titleLink);
			this.add(new MetaInfoPanel("metaInfo", articleEntity));
			this.add(new ExtendedLabel("teaser", articleEntity.getTeaser()));
			this.add(new ContentTagPanel<ArticleTagEntity>("tags", new ListModel<ArticleTagEntity>(articleEntity.getTags()), ArticlePage.class, params));

			final BookmarkablePageLink<ArticleViewPage> readMoreLink = new BookmarkablePageLink<ArticleViewPage>("readMoreLink", ArticleViewPage.class);
			readMoreLink.setParameter("0", articleEntity.getContentId());
			readMoreLink.setEnabled(allowedToRead);
			readMoreLink.add(new ExternalImage("readMoreImage", CommonConstants.REF_VIEW_IMG));
			final String labelKey = allowedToRead ? "readMore" : "loginToReadMore";
			final String label = new StringResourceModel(labelKey, ArticlePage.this, null).getString();
			readMoreLink.add(new Label("readMoreLabel", label));
			this.add(readMoreLink);
		}
	}

}
