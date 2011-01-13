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
package org.devproof.portal.core.app;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.injection.web.InjectorHolder;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.StringRequestTarget;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;
import org.apache.wicket.request.target.component.BookmarkablePageRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.common.page.NoStartPage;
import org.devproof.portal.core.module.mount.registry.MountHandlerRegistry;
import org.devproof.portal.core.module.mount.service.MountService;

/**
 * @author Carsten Hufe
 */
public class CustomMountUrlCodingStrategy implements IRequestTargetUrlCodingStrategy {
    private MountService mountService;

    public CustomMountUrlCodingStrategy(MountService mountService) {
        this.mountService = mountService;
    }

    @Override
    public String getMountPath() {
        return "doesNotMatter";
    }

    @Override
    public CharSequence encode(IRequestTarget requestTarget) {
        return null;
    }

    @Override
    public IRequestTarget decode(RequestParameters requestParameters) {
        return mountService.resolveRequestTarget(requestParameters.getPath());
//                        return new StringRequestTarget(requestParameters.toString());
//
//        try {
//            return new BookmarkablePageRequestTarget((Class<? extends Page>) Class.forName("org.devproof.portal.module.article.page.ArticleReadPage"), new PageParameters("0=Sample_article"));
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return new BookmarkablePageRequestTarget(NoStartPage.class);
//                        return new PageRequestTarget(new NoStartPage());
    }

    @Override
    public boolean matches(IRequestTarget requestTarget) {
        return true;
    }

    @Override
    public boolean matches(String path, boolean caseSensitive) {
        return true;
    }
}
