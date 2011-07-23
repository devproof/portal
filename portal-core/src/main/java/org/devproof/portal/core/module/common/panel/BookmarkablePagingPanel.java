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
package org.devproof.portal.core.module.common.panel;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.module.common.query.SearchQuery;

/**
 * @author Carsten Hufe
 */
public class BookmarkablePagingPanel extends Panel {

    private static final long serialVersionUID = 1L;
    public static final String PAGE_PARAM = "page";

    private IPageable pageable;
    private IModel<? extends SearchQuery> searchQueryModel;
    private Class<? extends Page> parentClazz;

    public BookmarkablePagingPanel(String id, IPageable pageable, IModel<? extends SearchQuery> searchQueryModel, Class<? extends Page> parentClazz) {
        super(id);
        this.pageable = pageable;
        this.searchQueryModel = searchQueryModel;
        this.parentClazz = parentClazz;
        add(createBackLink());
        add(createForwardLink());
    }

    private BookmarkablePageLink<String> createForwardLink() {
        return new BookmarkablePageLink<String>("forwardLink", parentClazz) {
            private static final long serialVersionUID = 1L;

            @Override
            public PageParameters getPageParameters() {
                SearchQuery resolver = searchQueryModel.getObject();
                PageParameters pageParameters = resolver.getPageParameters();
                pageParameters.add(PAGE_PARAM, pageable.getCurrentPage() + 2);
                return pageParameters;
            }

            @Override
            public boolean isVisible() {
                return (pageable.getPageCount() - 1) > pageable.getCurrentPage();
            }
        };
    }

    private BookmarkablePageLink<String> createBackLink() {
        return new BookmarkablePageLink<String>("backLink", parentClazz) {
            private static final long serialVersionUID = 1L;

            @Override
            public PageParameters getPageParameters() {
                SearchQuery resolver = searchQueryModel.getObject();
                PageParameters pageParameters = resolver.getPageParameters();
                pageParameters.add(PAGE_PARAM, pageable.getCurrentPage());
                return pageParameters;
            }

            @Override
            public boolean isVisible() {
                return pageable.getCurrentPage() != 0;
            }
        };
    }
}
