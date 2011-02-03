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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.devproof.portal.core.module.mount.annotation.MountPointHandler;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.core.module.mount.service.MountService;
import org.devproof.portal.module.article.page.ArticlePrintPage;
import org.devproof.portal.module.article.page.ArticleReadPage;
import org.devproof.portal.module.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Carsten Hufe
 */
// TODO unit test
@MountPointHandler("articleMountHandler")
public class ArticleMountHandler implements MountHandler {
    private ArticleService articleService;
    private MountService mountService;

    @Override
    @Transactional(readOnly = true)
    public IRequestTarget getRequestTarget(String requestedUrl, MountPoint mountPoint) {
        String relatedContentId = mountPoint.getRelatedContentId();
        PageParameters pageParameters = new PageParameters("0=" + relatedContentId);
        String rest = StringUtils.substringAfter(requestedUrl, mountPoint.getMountPath());
        if(StringUtils.isNotBlank(rest)) {
            String page = StringUtils.remove(rest, '/');
            if("print".equals(page)) {
                return new BookmarkablePageRequestTarget(ArticlePrintPage.class, pageParameters);
            }
            else if(StringUtils.isNumeric(page)) {
                pageParameters.put("1", page);
            }
        }
        return new BookmarkablePageRequestTarget(ArticleReadPage.class, pageParameters);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canHandlePageClass(Class<? extends Page> pageClazz, PageParameters pageParameters) {
        if(ArticleReadPage.class.equals(pageClazz) || ArticlePrintPage.class.equals(pageClazz)) {
            String articleId = pageParameters.getString("0");
            if(StringUtils.isNumeric(articleId)) {
                return mountService.existsMountPoint(articleId, getHandlerKey());
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public String urlFor(Class<? extends Page> pageClazz, PageParameters params) {
        String relatedContentId = params.getString("0");
        MountPoint mountPoint = mountService.findDefaultMountPoint(relatedContentId, getHandlerKey());
        if(mountPoint != null) {
            String mountPath = mountPoint.getMountPath();
            if(ArticleReadPage.class.equals(pageClazz)) {
                if(params.containsKey("1")) {
                    mountPath += "/" + params.getString("1");
                }
                return mountPath; // page
            }
            else if(ArticlePrintPage.class.equals(pageClazz)) {
                return mountPath + "/print";
            }
        }
        return null;
    }

    @Override
    public String getHandlerKey() {
        return "article";
    }

    @Autowired
    public void setMountService(MountService mountService) {
        this.mountService = mountService;
    }

    @Autowired
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }
}
