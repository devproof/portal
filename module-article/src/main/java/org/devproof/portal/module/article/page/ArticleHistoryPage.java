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

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.dataprovider.QueryDataProvider;
import org.devproof.portal.core.module.historization.page.AbstractHistoryPage;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.entity.ArticleHistorized;
import org.devproof.portal.module.article.panel.ArticlePrintPanel;
import org.devproof.portal.module.article.query.ArticleHistoryQuery;
import org.devproof.portal.module.article.service.ArticleService;

import java.text.SimpleDateFormat;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/admin/article/history")
public class ArticleHistoryPage extends AbstractHistoryPage<ArticleHistorized> {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "articleService")
    private ArticleService articleService;
    @SpringBean(name = "articleHistoryDataProvider")
    private QueryDataProvider<ArticleHistorized, ArticleHistoryQuery> articleHistoryDataProvider;
    @SpringBean(name = "displayDateFormat")
    private SimpleDateFormat dateFormat;
    private IModel<ArticleHistoryQuery> queryModel;
    private IModel<Article> articleModel;
    private PageParameters params;

    public ArticleHistoryPage(PageParameters params) {
        super(params);
        this.params = params;
        this.queryModel = articleHistoryDataProvider.getSearchQueryModel();
    }

    private IModel<Article> getArticleModel() {
        if(articleModel == null) {
            articleModel = createArticleModel();
        }
        return articleModel;
    }

    private LoadableDetachableModel<Article> createArticleModel() {
        return new LoadableDetachableModel<Article>() {
            private static final long serialVersionUID = -4042346265134003874L;

            @Override
            protected Article load() {
                return articleService.findById(params.getAsInteger("id"));
            }
        };
    }

    @Override
    protected void onBeforeRender() {
        this.queryModel.getObject().setArticle(getArticleModel().getObject());
        super.onBeforeRender();
    }

    @Override
    protected IModel<String> newHeadlineModel() {
        return new StringResourceModel("headline", this, new PropertyModel<String>(getArticleModel(), "title"));
    }

    @Override
    protected QueryDataProvider<ArticleHistorized, ?> getQueryDataProvider() {
        return articleHistoryDataProvider;
    }

    @Override
    protected Component newHistorizedView(String markupId, IModel<ArticleHistorized> historizedModel) {
        return new ArticlePrintPanel(markupId, new PropertyModel<Article>(historizedModel, "convertedArticle"));
    }

    @Override
    protected void onRestore(IModel<ArticleHistorized> restoreModel) {
        articleService.restoreFromHistory(restoreModel.getObject());
        info(getString("restored"));
        Integer articleId = restoreModel.getObject().getArticle().getId();
        setResponsePage(new ArticlePage(new PageParameters("id=" + articleId)));
    }
}
