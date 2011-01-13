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
package org.devproof.portal.module.article.mount;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.target.basic.StringRequestTarget;
import org.apache.wicket.request.target.component.PageRequestTarget;
import org.devproof.portal.core.module.mount.annotation.MountPointHandler;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.module.article.entity.Article;
import org.devproof.portal.module.article.page.ArticlePage;
import org.devproof.portal.module.article.page.ArticleReadPage;
import org.devproof.portal.module.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Carsten Hufe
 */
// TODO unit test
@MountPointHandler("articleMountHandler")
public class ArticleMountHandler implements MountHandler {
    private ArticleService articleService;

    @Override
    public IRequestTarget getRequestTarget(String requestedUrl, MountPoint mountPoint) {
        String relatedContentId = mountPoint.getRelatedContentId();
        // TODO ArticlePage laden ...
        Article article = articleService.findById(Integer.valueOf(relatedContentId));
//        return new Article;
        return new PageRequestTarget(new ArticleReadPage(new PageParameters("0=" + article.getContentId())));
    }

    @Override
    public String getHandlerKey() {
        return "article";
    }

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }
}
