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

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
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

	public ArticlePage(final PageParameters params) {
		super(params);
		ArticleQuery query = createArticleQuery();
		ArticleDataView dataView = createArticleDataView(params);
		add(dataView);
		add(createPagingPanel(params, dataView));
		addFilterBox(createArticleSearchBoxPanel(params, query, dataView));
		addTagCloudBox(articleTagService, new PropertyModel<ArticleTagEntity>(query, "tag"), ArticlePage.class, params);
		addSyntaxHighlighter();
	}

	private ArticleSearchBoxPanel createArticleSearchBoxPanel(final PageParameters params, final ArticleQuery query,
			final ArticleDataView dataView) {
		return new ArticleSearchBoxPanel("box", query, articleDataProvider, this, dataView, params);
	}

	private ArticleDataView createArticleDataView(final PageParameters params) {
		return new ArticleDataView("listArticle", params);
	}

	private BookmarkablePagingPanel createPagingPanel(final PageParameters params, final ArticleDataView dataView) {
		return new BookmarkablePagingPanel("paging", dataView, ArticlePage.class, params);
	}

	private ArticleQuery createArticleQuery() {
		ArticleQuery query = new ArticleQuery();
		PortalSession session = (PortalSession) getSession();
		if (!session.hasRight("article.view")) {
			query.setRole(session.getRole());
		}
		articleDataProvider.setQueryObject(query);
		return query;
	}

	private class ArticleDataView extends DataView<ArticleEntity> {
		private static final long serialVersionUID = 1L;
		private final boolean onlyOneArticleInResult;
		private final PageParameters params;

		public ArticleDataView(final String id, final PageParameters params) {
			super(id, articleDataProvider);
			this.params = params;
			onlyOneArticleInResult = articleDataProvider.size() == 1;
			setItemsPerPage(configurationService.findAsInteger(ArticleConstants.CONF_ARTICLES_PER_PAGE));
		}

		@Override
		protected void populateItem(final Item<ArticleEntity> item) {
			ArticleEntity article = item.getModelObject();
			item.setOutputMarkupId(true);
			if (onlyOneArticleInResult) {
				setPageTitle(article.getTitle());
			}
			item.add(createArticleView(item));
		}

		private ArticleView createArticleView(final Item<ArticleEntity> item) {
			ArticleEntity article = item.getModelObject();
			ArticleView articleViewPanel = new ArticleView("articleView", article, params, item);
			return articleViewPanel;
		}
	}

	/**
	 * Shows an article teaser
	 */
	private class ArticleView extends Fragment {

		private static final long serialVersionUID = 1L;

		public ArticleView(final String id, final ArticleEntity articleEntity, final PageParameters params,
				final Item<ArticleEntity> item) {
			super(id, "articleView", ArticlePage.this);
			boolean allowedToRead = isAllowedToRead(articleEntity);
			add(createAppropriateAuthorPanel(item));
			add(createTitleLink(articleEntity, allowedToRead));
			add(createMetaInfoPanel(articleEntity));
			add(createTeaserLabel(articleEntity));
			add(createTagPanel(articleEntity, params));
			add(createReadMoreLink(articleEntity, allowedToRead));
		}

		private Component createAppropriateAuthorPanel(final Item<ArticleEntity> item) {
			if (isAuthor()) {
				return createAuthorPanel(item);
			} else {
				return createEmptyAuthorPanel();
			}
		}

		private BookmarkablePageLink<ArticleReadPage> createReadMoreLink(final ArticleEntity articleEntity,
				final boolean allowedToRead) {
			BookmarkablePageLink<ArticleReadPage> readMoreLink = new BookmarkablePageLink<ArticleReadPage>(
					"readMoreLink", ArticleReadPage.class);
			readMoreLink.setParameter("0", articleEntity.getContentId());
			readMoreLink.setEnabled(allowedToRead);
			readMoreLink.add(new Image("readMoreImage", CommonConstants.REF_VIEW_IMG));
			String labelKey = allowedToRead ? "readMore" : "loginToReadMore";
			String label = new StringResourceModel(labelKey, ArticlePage.this, null).getString();
			readMoreLink.add(new Label("readMoreLabel", label));
			return readMoreLink;
		}

		private ContentTagPanel<ArticleTagEntity> createTagPanel(final ArticleEntity articleEntity,
				final PageParameters params) {
			return new ContentTagPanel<ArticleTagEntity>("tags", new ListModel<ArticleTagEntity>(articleEntity
					.getTags()), ArticlePage.class, params);
		}

		private ExtendedLabel createTeaserLabel(final ArticleEntity articleEntity) {
			return new ExtendedLabel("teaser", articleEntity.getTeaser());
		}

		private MetaInfoPanel createMetaInfoPanel(final ArticleEntity articleEntity) {
			return new MetaInfoPanel("metaInfo", articleEntity);
		}

		private WebMarkupContainer createEmptyAuthorPanel() {
			return new WebMarkupContainer("authorButtons");
		}

		private boolean isAllowedToRead(final ArticleEntity articleEntity) {
			PortalSession session = (PortalSession) getSession();
			boolean allowedToRead = session.hasRight("article.read") || session.hasRight(articleEntity.getReadRights());
			return allowedToRead;
		}

		private BookmarkablePageLink<ArticleReadPage> createTitleLink(final ArticleEntity articleEntity,
				final boolean allowedToRead) {
			final BookmarkablePageLink<ArticleReadPage> titleLink = new BookmarkablePageLink<ArticleReadPage>(
					"titleLink", ArticleReadPage.class);
			titleLink.setParameter("0", articleEntity.getContentId());
			titleLink.setEnabled(allowedToRead);
			titleLink.add(new Label("titleLabel", articleEntity.getTitle()));
			return titleLink;
		}

		private AuthorPanel<ArticleEntity> createAuthorPanel(final Item<ArticleEntity> item) {
			final ArticleEntity article = item.getModelObject();
			return new AuthorPanel<ArticleEntity>("authorButtons", article) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(final AjaxRequestTarget target) {
					articleService.delete(getEntity());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(final AjaxRequestTarget target) {
					// Reload because LazyIntialization occur
					final ArticleEntity tmp = articleService.findByIdAndPrefetch(article.getId());
					setResponsePage(new ArticleEditPage(tmp));
				}
			};
		}
	}

}
