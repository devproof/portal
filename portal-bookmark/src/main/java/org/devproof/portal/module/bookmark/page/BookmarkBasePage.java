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

import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.panel.DeliciousSyncPanel;
import org.devproof.portal.module.bookmark.service.BookmarkService;
import org.devproof.portal.module.deadlinkcheck.panel.DeadlinkCheckPanel;

/**
 * @author Carsten Hufe
 */
public abstract class BookmarkBasePage extends TemplatePage {

	private static final long serialVersionUID = 1L;
	@SpringBean(name = "bookmarkService")
	private BookmarkService bookmarkService;
	private WebMarkupContainer modalWindow;
	private final boolean isAuthor;

	public BookmarkBasePage(final PageParameters params) {
		super(params);
		this.add(CSSPackageResource.getHeaderContribution(BookmarkConstants.REF_BOOKMARK_CSS));
		PortalSession session = (PortalSession) getSession();
		isAuthor = session.hasRight("page.BookmarkEditPage");
		if (isAuthor) {
			modalWindow = new ModalWindow("modalWindow");
			Link<?> addLink = new Link<Object>("adminLink") {

				private static final long serialVersionUID = 1L;

				@Override
				public void onClick() {
					final BookmarkEntity newBookmark = bookmarkService.newBookmarkEntity();
					setResponsePage(new BookmarkEditPage(newBookmark));
				}
			};
			addLink.add(new Label("linkName", this.getString("createLink")));
			addPageAdminBoxLink(addLink);

			AjaxLink<BookmarkEntity> deadlinkCheckLink = new AjaxLink<BookmarkEntity>("adminLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					List<BookmarkEntity> allBookmarks = bookmarkService.findAll();
					ModalWindow modalWindow = (ModalWindow) BookmarkBasePage.this.modalWindow;
					final DeadlinkCheckPanel<BookmarkEntity> deadLinkPanel = new DeadlinkCheckPanel<BookmarkEntity>(
							modalWindow.getContentId(), "bookmark", allBookmarks) {
						private static final long serialVersionUID = 1L;

						@Override
						public void onBroken(final BookmarkEntity brokenEntity) {
							bookmarkService.markBrokenBookmark(brokenEntity);
						}

						@Override
						public void onValid(final BookmarkEntity validEntity) {
							bookmarkService.markValidBookmark(validEntity);
						}
					};
					modalWindow.setInitialHeight(300);
					modalWindow.setInitialWidth(500);
					modalWindow.setContent(deadLinkPanel);
					modalWindow.show(target);
				}
			};
			deadlinkCheckLink.add(new Label("linkName", this.getString("deadlinkCheckLink")));
			addPageAdminBoxLink(deadlinkCheckLink);

			// Synchronize from del.icio.us
			AjaxLink<BookmarkEntity> syncLink = new AjaxLink<BookmarkEntity>("adminLink") {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(final AjaxRequestTarget target) {
					ModalWindow modalWindow = (ModalWindow) BookmarkBasePage.this.modalWindow;
					DeliciousSyncPanel syncPanel = new DeliciousSyncPanel(modalWindow.getContentId());
					modalWindow.setInitialHeight(600);
					modalWindow.setInitialWidth(800);
					modalWindow.setContent(syncPanel);
					modalWindow.show(target);
				}
			};
			syncLink.add(new Label("linkName", this.getString("syncLink")));
			addPageAdminBoxLink(syncLink);
		} else {
			modalWindow = new WebMarkupContainer("modalWindow");
			modalWindow.setVisible(false);
		}
		add(modalWindow);
	}

	public WebMarkupContainer getModalWindow() {
		return modalWindow;
	}

	public boolean isAuthor() {
		return isAuthor;
	}
}
