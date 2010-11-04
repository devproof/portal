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
package org.devproof.portal.module.article.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.NavigationBox;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.page.ArticlePage;
import org.devproof.portal.module.article.service.ArticleService;

import java.util.List;

/**
 * Latest article box
 *
 * @author Carsten Hufe
 */
@NavigationBox("Latest Article Box")
public class ArticleBoxPanel extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "articleService")
    private ArticleService articleService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private WebMarkupContainer titleContainer;
    private IModel<List<Article>> latestArticlesModel;

    public ArticleBoxPanel(String id) {
        super(id);
        latestArticlesModel = createLatestArticlesModel();
        add(createTitleContainer());
        add(createRepeatingArticles());
    }

    @Override
    public boolean isVisible() {
        List<Article> articles = latestArticlesModel.getObject();
        return articles.size() > 0;
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        return titleContainer;
    }

    private IModel<List<Article>> createLatestArticlesModel() {
        return new LoadableDetachableModel<List<Article>>() {
            private static final long serialVersionUID = -8763260134372373780L;

            @Override
            protected List<Article> load() {
                Integer numberOfLatestArticles = configurationService.findAsInteger(ArticleConstants.CONF_BOX_NUM_LATEST_ARTICLES);
                PortalSession session = (PortalSession) getSession();
                return articleService.findAllArticlesForRoleOrderedByDateDesc(session.getRole(), 0, numberOfLatestArticles);
            }
        };
    }

    private ListView<Article> createRepeatingArticles() {
        return new ListView<Article>("repeatingArticles", latestArticlesModel) {
            private static final long serialVersionUID = 3388745835706671920L;

            @Override
            protected void populateItem(ListItem<Article> item) {
                item.add(createLinkToArticle(item.getModel()));
            }
        };
    }

    private BookmarkablePageLink<ArticlePage> createLinkToArticle(IModel<Article> articleModel) {
        Article article = articleModel.getObject();
        BookmarkablePageLink<ArticlePage> link = new BookmarkablePageLink<ArticlePage>("link", ArticlePage.class);
        link.setParameter("id", article.getId());
        link.add(createLinkToArticleLabel(articleModel));
        return link;
    }

    private Label createLinkToArticleLabel(IModel<Article> articleModel) {
        return new Label("linkName", new PropertyModel<String>(articleModel, "title"));
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }
}
