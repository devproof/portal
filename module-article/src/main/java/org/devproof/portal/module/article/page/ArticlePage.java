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
package org.devproof.portal.module.article.page;

import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.component.AutoPagingDataView;
import org.devproof.portal.core.module.common.component.ExtendedLabel;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.common.panel.AuthorPanel;
import org.devproof.portal.core.module.common.panel.BookmarkablePagingPanel;
import org.devproof.portal.core.module.common.panel.MetaInfoPanel;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.print.PrintConstants;
import org.devproof.portal.core.module.tag.panel.TagContentPanel;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.entity.ArticleTagEntity;
import org.devproof.portal.module.article.panel.ArticleSearchBoxPanel;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.devproof.portal.module.article.service.ArticleService;
import org.devproof.portal.module.article.service.ArticleTagService;
import org.devproof.portal.module.comment.config.DefaultCommentConfiguration;
import org.devproof.portal.module.comment.panel.ExpandableCommentPanel;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/articles", registerMainNavigationLink = true)
public class ArticlePage extends ArticleBasePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "articleService")
	private ArticleService articleService;
	@SpringBean(name = "articleDataProvider")
	private QueryDataProvider<ArticleEntity, ArticleQuery> articleDataProvider;
	@SpringBean(name = "articleTagService")
	private ArticleTagService articleTagService;
	@SpringBean(name = "configurationService")
	private ConfigurationService configurationService;

	private ArticleDataView dataView;
	private IModel<ArticleQuery> searchQueryModel;

	public ArticlePage(PageParameters params) {
		super(params);
		searchQueryModel = articleDataProvider.getSearchQueryModel();
		add(createRepeatingArticles());
		add(createPagingPanel());
		addFilterBox(createArticleSearchBoxPanel());
		addTagCloudBox();
	}

	private void addTagCloudBox() {
		addTagCloudBox(articleTagService, ArticlePage.class);
	}

	private ArticleSearchBoxPanel createArticleSearchBoxPanel() {
		return new ArticleSearchBoxPanel(getBoxId(), searchQueryModel);
	}

	private ArticleDataView createRepeatingArticles() {
		dataView = new ArticleDataView("repeatingArticles");
		return dataView;
	}

	private BookmarkablePagingPanel createPagingPanel() {
		return new BookmarkablePagingPanel("paging", dataView, searchQueryModel, ArticlePage.class);
	}

	@Override
	public String getPageTitle() {
		if (articleDataProvider.size() == 1) {
			Iterator<? extends ArticleEntity> it = articleDataProvider.iterator(0, 1);
			ArticleEntity article = it.next();
			return article.getTitle();
		}
		return "";
	}

	private class ArticleDataView extends AutoPagingDataView<ArticleEntity> {
		private static final long serialVersionUID = 1L;

		public ArticleDataView(String id) {
			super(id, articleDataProvider);
			setItemsPerPage(configurationService.findAsInteger(ArticleConstants.CONF_ARTICLES_PER_PAGE));
			setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
		}

		@Override
		protected void populateItem(Item<ArticleEntity> item) {
			item.add(createArticleView(item));
			item.setOutputMarkupId(true);
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
		private IModel<ArticleEntity> articleModel;
		private boolean allowedToRead = false;

		public ArticleView(String id, Item<ArticleEntity> item) {
			super(id, "articleView", ArticlePage.this);
			articleModel = item.getModel();
			allowedToRead = isAllowedToRead(articleModel);
			add(createAppropriateAuthorPanel(item));
			add(createTitleLink());
			add(createMetaInfoPanel());
			add(createPrintLink());
			add(createTeaserLabel());
			add(createTagPanel());
			add(createReadMoreLink());
			add(createCommentPanel());
		}

		private Component createCommentPanel() {
			ArticleEntity article = articleModel.getObject();
			DefaultCommentConfiguration conf = new DefaultCommentConfiguration();
			conf.setModuleContentId(article.getId().toString());
			conf.setModuleName(ArticlePage.class.getSimpleName());
			conf.setViewRights(article.getCommentViewRights());
			conf.setWriteRights(article.getCommentWriteRights());
			return new ExpandableCommentPanel("comments", conf);
		}

		private Component createPrintLink() {
			ArticleEntity article = articleModel.getObject();
			PageParameters params = new PageParameters("0=" + article.getContentId());
			BookmarkablePageLink<ArticlePrintPage> link = new BookmarkablePageLink<ArticlePrintPage>("printLink",
					ArticlePrintPage.class, params);
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
			ArticleEntity article = articleModel.getObject();
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
			IModel<String> readMoreModel = createReadMoreModel();
			return new Label("readMoreLabel", readMoreModel);
		}

		private AbstractReadOnlyModel<String> createReadMoreModel() {
			return new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 118766734564336104L;

				@Override
				public String getObject() {
					String labelKey = allowedToRead ? "readMore" : "loginToReadMore";
					return ArticlePage.this.getString(labelKey);
				}
			};
		}

		private TagContentPanel<ArticleTagEntity> createTagPanel() {
			IModel<List<ArticleTagEntity>> tagModel = new PropertyModel<List<ArticleTagEntity>>(articleModel, "tags");
			return new TagContentPanel<ArticleTagEntity>("tags", tagModel, ArticlePage.class);
		}

		private ExtendedLabel createTeaserLabel() {
			IModel<String> teaserModel = new PropertyModel<String>(articleModel, "teaser");
			return new ExtendedLabel("teaser", teaserModel);
		}

		private MetaInfoPanel<?> createMetaInfoPanel() {
			return new MetaInfoPanel<ArticleEntity>("metaInfo", articleModel);
		}

		private WebMarkupContainer createEmptyAuthorPanel() {
			return new WebMarkupContainer("authorButtons");
		}

		private boolean isAllowedToRead(IModel<ArticleEntity> articleModel) {
			ArticleEntity article = articleModel.getObject();
			PortalSession session = (PortalSession) getSession();
			return session.hasRight("article.read") || session.hasRight(article.getReadRights());
		}

		private BookmarkablePageLink<ArticleReadPage> createTitleLink() {
			ArticleEntity article = articleModel.getObject();
			BookmarkablePageLink<ArticleReadPage> titleLink = new BookmarkablePageLink<ArticleReadPage>("titleLink",
					ArticleReadPage.class);
			titleLink.add(createTitleLabel());
			titleLink.setParameter("0", article.getContentId());
			titleLink.setEnabled(allowedToRead);
			return titleLink;
		}

		private Label createTitleLabel() {
			IModel<String> titleModel = new PropertyModel<String>(articleModel, "title");
			return new Label("titleLabel", titleModel);
		}

		private AuthorPanel<ArticleEntity> createAuthorPanel(final Item<ArticleEntity> item) {
			return new AuthorPanel<ArticleEntity>("authorButtons", articleModel) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onDelete(AjaxRequestTarget target) {
					articleService.delete(getEntityModel().getObject());
					item.setVisible(false);
					target.addComponent(item);
					target.addComponent(getFeedback());
					info(getString("msg.deleted"));
				}

				@Override
				public void onEdit(AjaxRequestTarget target) {
					IModel<ArticleEntity> articleModel = createArticleModel();
					setResponsePage(new ArticleEditPage(articleModel));
				}

				private IModel<ArticleEntity> createArticleModel() {
					return new LoadableDetachableModel<ArticleEntity>() {
						private static final long serialVersionUID = 1L;

						@Override
						protected ArticleEntity load() {
							ArticleEntity article = articleModel.getObject();
							return articleService.findById(article.getId());
						}
					};
				}
			};
		}
	}
}
