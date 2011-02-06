/*
 * Copyright 2009-2011 Carsten Hufe devproof.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.devproof.portal.module.blog.page;

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
import org.devproof.portal.module.blog.entity.Blog;
import org.devproof.portal.module.blog.panel.BlogPrintPanel;
import org.devproof.portal.module.blog.service.BlogService;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/print/blog", indexMountedPath = true)
public class BlogPrintPage extends PrintPage {
    private static final long serialVersionUID = -861792869467871383L;
    @SpringBean(name = "blogService")
    private BlogService blogService;
    private IModel<Blog> blogModel;
    private PageParameters params;

    public BlogPrintPage(PageParameters params) {
        super(params);
    }

    private LoadableDetachableModel<Blog> createBlogModel() {
        return new LoadableDetachableModel<Blog>() {
            private static final long serialVersionUID = 2758949172939182113L;

            @Override
            protected Blog load() {
                Integer blogId = getBlogId();
                return blogService.findById(blogId);
            }
        };
    }

    @Override
    protected void onBeforeRender() {
        validateAccessRights();
        super.onBeforeRender();
    }

    private IModel<Blog> getBlogModel() {
        if (blogModel == null) {
            blogModel = createBlogModel();
        }
        return blogModel;
    }

    @Override
    protected Component createPrintableComponent(String id, PageParameters params) {
        this.params = params;
        return new BlogPrintPanel(id, getBlogModel());
    }

    private Integer getBlogId() {
        Integer blogId = params.getAsInteger("0");
        if (blogId == null) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.parameter")));
        }
        return blogId;
    }

    private void validateAccessRights() {
        Blog blog = getBlogModel().getObject();
        if (blog == null || !isAllowedToRead(blog)) {
            throw new RestartResponseAtInterceptPageException(MessagePage.getMessagePage(getString("missing.right")));
        }
    }

    private boolean isAllowedToRead(Blog blog) {
        PortalSession session = (PortalSession) getSession();
        return session.hasRight(blog.getViewRights()) || session.hasRight("blog.view");
    }
}
