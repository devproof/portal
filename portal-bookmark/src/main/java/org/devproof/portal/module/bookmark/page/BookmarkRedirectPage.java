/*
 * Copyright 2009 Carsten Hufe devproof.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
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

	public BookmarkRedirectPage(final PageParameters params) {
		super(params);
		PortalSession session = (PortalSession) getSession();
		if (params.containsKey("0")) {
			BookmarkEntity bookmarkEntity = this.bookmarkService.findById(params.getAsInteger("0", 0));
			if (bookmarkEntity != null && session.hasRight("bookmark.visit", bookmarkEntity.getVisitRights())) {
				this.bookmarkService.incrementHits(bookmarkEntity);
				getRequestCycle().setRequestTarget(new RedirectRequestTarget(bookmarkEntity.getUrl()));
			}
		}
	}
}
