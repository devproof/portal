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
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.page.MessagePage;
import org.devproof.portal.core.module.print.page.PrintPage;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.panel.ArticlePrintPanel;
import org.devproof.portal.module.article.service.ArticleService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/print/article", indexMountedPath = true)
public class ArticlePrintPage extends PrintPage {
    private static final long serialVersionUID = 3988970146526291830L;
    @SpringBean(name = "articleService")
    private ArticleService articleService;
    private IModel<ArticleEntity> articleModel;
    private PageParameters params;

    public ArticlePrintPage(PageParameters params) {
        super(params);
    }

    private LoadableDetachableModel<ArticleEntity> createArticleModel() {
        return new LoadableDetachableModel<ArticleEntity>() {
            private static final long serialVersionUID = 1826109490689274522L;

            @Override
            protected ArticleEntity load() {
                String contentId = getContentId();
                return articleService.findByContentId(contentId);
            }
        };
    }

    private IModel<ArticleEntity> getArticleModel() {
        if (articleModel == null) {
            articleModel = createArticleModel();
        }
        return articleModel;
    }

    @Override
    protected Component createPrintableComponent(String id, PageParameters params) {
        this.params = params;
        return new ArticlePrintPanel(id, getArticleModel());
    }

    @Override
    protected void onBeforeRender() {
        validateAccessRights();
        super.onBeforeRender();
    }

    private String getContentId() {
        String contentId = params.getString("0");
        if (contentId == null) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.parameter")));
        }
        return contentId;
    }

    private void validateAccessRights() {
        ArticleEntity article = getArticleModel().getObject();
        if (article == null || !isAllowedToRead(article)) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right")));
        }
    }

    private boolean isAllowedToRead(ArticleEntity article) {
        PortalSession session = (PortalSession) getSession();
        return session.hasRight(article.getReadRights()) || session.hasRight("article.read");
    }
}
