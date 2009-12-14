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
import org.devproof.portal.core.module.print.PrintConstants;
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

	private ArticleDataView dataView;
	private ArticleQuery query;
	private PageParameters params;

	public ArticlePage(PageParameters params) {
		super(params);
		this.params = params;
		createArticleQuery();
		add(createArticleDataView());
		add(createPagingPanel());
		addFilterBox(createArticleSearchBoxPanel());
		addTagCloudBox();
	}

	private void addTagCloudBox() {
		addTagCloudBox(articleTagService, new PropertyModel<ArticleTagEntity>(query, "tag"), ArticlePage.class, params);
	}

	private ArticleSearchBoxPanel createArticleSearchBoxPanel() {
		return new ArticleSearchBoxPanel("box", query, articleDataProvider, this, dataView, params);
	}

	private ArticleDataView createArticleDataView() {
		dataView = new ArticleDataView("listArticle");
		return dataView;
	}

	private BookmarkablePagingPanel createPagingPanel() {
		return new BookmarkablePagingPanel("paging", dataView, ArticlePage.class, params);
	}

	private ArticleQuery createArticleQuery() {
		query = new ArticleQuery();
		PortalSession session = (PortalSession) getSession();
		if (!session.hasRight("article.view")) {
			query.setRole(session.getRole());
		}
		articleDataProvider.setQueryObject(query);
		return query;
	}

	private class ArticleDataView extends DataView<ArticleEntity> {
		private static final long serialVersionUID = 1L;
		private boolean onlyOneArticleInResult;

		public ArticleDataView(String id) {
			super(id, articleDataProvider);
			onlyOneArticleInResult = articleDataProvider.size() == 1;
			setItemsPerPage(configurationService.findAsInteger(ArticleConstants.CONF_ARTICLES_PER_PAGE));
		}

		@Override
		protected void populateItem(Item<ArticleEntity> item) {
			setArticleTitleAsPageTitle(item);
			item.setOutputMarkupId(true);
			item.add(createArticleView(item));
		}

		private void setArticleTitleAsPageTitle(Item<ArticleEntity> item) {
			if (onlyOneArticleInResult) {
				ArticleEntity article = item.getModelObject();
				setPageTitle(article.getTitle());
			}
		}

		private ArticleView createArticleView(Item<ArticleEntity> item) {
			return new ArticleView("articleView", item);
		}
	}

	/**
	 * Shows an article teaser
	 */
	private class ArticleView extends Fragment {

		private static final long serialVersionUID = 1L;
		private ArticleEntity article;
		private boolean allowedToRead = false;

		public ArticleView(String id, Item<ArticleEntity> item) {
			super(id, "articleView", ArticlePage.this);
			article = item.getModelObject();
			allowedToRead = isAllowedToRead(article);
			add(createAppropriateAuthorPanel(item));
			add(createTitleLink());
			add(createMetaInfoPanel());
			add(createPrintLink());
			add(createTeaserLabel());
			add(createTagPanel());
			add(createReadMoreLink());
		}

		private Component createPrintLink() {
			BookmarkablePageLink<ArticlePrintPage> link = new BookmarkablePageLink<ArticlePrintPage>("printLink",
					ArticlePrintPage.class, new PageParameters("0=" + article.getContentId()));
			link.add(createPrintImage());
			link.setVisible(allowedToRead);
			return link;
		}

		private Component createPrintImage() {
			return new Image("printImage", PrintConstants.REF_PRINTER_IMG);
		}

		private Component createAppropriateAuthorPanel(Item<ArticleEntity> item) {
			if (isAuthor()) {
				return createAuthorPanel(item);
			} else {
				return createEmptyAuthorPanel();
			}
		}

		private BookmarkablePageLink<ArticleReadPage> createReadMoreLink() {
			BookmarkablePageLink<ArticleReadPage> readMoreLink = new BookmarkablePageLink<ArticleReadPage>(
					"readMoreLink", ArticleReadPage.class);
			readMoreLink.add(createReadMoreImage());
			readMoreLink.add(createReadMoreLabel());
			readMoreLink.setParameter("0", article.getContentId());
			readMoreLink.setEnabled(allowedToRead);
			return readMoreLink;
		}

		private Image createReadMoreImage() {
			return new Image("readMoreImage", CommonConstants.REF_VIEW_IMG);
		}

		private Label createReadMoreLabel() {
			String labelKey = allowedToRead ? "readMore" : "loginToReadMore";
			return new Label("readMoreLabel", ArticlePage.this.getString(labelKey));
		}

		private ContentTagPanel<ArticleTagEntity> createTagPanel() {
			return new ContentTagPanel<ArticleTagEntity>("tags", new ListModel<ArticleTagEntity>(article.getTags()),
					ArticlePage.class, params);
		}

		private ExtendedLabel createTeaserLabel() {
			return new ExtendedLabel("teaser", article.getTeaser());
		}

		private MetaInfoPanel createMetaInfoPanel() {
			return new MetaInfoPanel("metaInfo", article);
		}

		private WebMarkupContainer createEmptyAuthorPanel() {
			return new WebMarkupContainer("authorButtons");
		}

		private boolean isAllowedToRead(ArticleEntity articleEntity) {
			PortalSession session = (PortalSession) getSession();
			return session.hasRight("article.read") || session.hasRight(articleEntity.getReadRights());
		}

		private BookmarkablePageLink<ArticleReadPage> createTitleLink() {
			BookmarkablePageLink<ArticleReadPage> titleLink = new BookmarkablePageLink<ArticleReadPage>("titleLink",
					ArticleReadPage.class);
			titleLink.setParameter("0", article.getContentId());
			titleLink.setEnabled(allowedToRead);
			titleLink.add(createTitleLabel());
			return titleLink;
		}

		private Label createTitleLabel() {
			return new Label("titleLabel", article.getTitle());
		}

		private AuthorPanel<ArticleEntity> createAuthorPanel(final Item<ArticleEntity> item) {
			final ArticleEntity article = item.getModelObject();
			return new AuthorPanel<ArticleEntity>("authorButtons", article) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					articleService.delete(getEntity());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(AjaxRequestTarget target) {
					// Reload because LazyIntialization occur
					ArticleEntity tmp = articleService.findByIdAndPrefetch(article.getId());
					setResponsePage(new ArticleEditPage(tmp));
				}
			};
		}
	}

}
