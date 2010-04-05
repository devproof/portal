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
package org.devproof.portal.module.bookmark.page;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.service.BookmarkService;

/**
 * @author Carsten Hufe
 */
public class BookmarkRedirectPage extends WebPage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "bookmarkService")
    private BookmarkService bookmarkService;

    private PageParameters params;

    public BookmarkRedirectPage(PageParameters params) {
        super(params);
        this.params = params;
    }

    @Override
    protected void onBeforeRender() {
        if (hasFirstParameter()) {
            BookmarkEntity bookmark = bookmarkService.findById(getBookmarkIdParam());
            if (hasVisitRight(bookmark)) {
                bookmarkService.incrementHits(bookmark);
                redirectTo(bookmark);
            }
        }
        super.onBeforeRender();
    }

    private int getBookmarkIdParam() {
        return params.getAsInteger("0", 0);
    }

    private void redirectTo(BookmarkEntity bookmark) {
        getRequestCycle().setRequestTarget(new RedirectRequestTarget(bookmark.getUrl()));
    }

    private boolean hasFirstParameter() {
        return params.containsKey("0");
    }

    private boolean hasVisitRight(BookmarkEntity bookmark) {
        if (bookmark == null) {
            return false;
        }
        PortalSession session = (PortalSession) getSession();
        return session.hasRight("bookmark.visit", bookmark.getVisitRights());
    }
}
