/*
 * Copyright 2009-2010 Carsten Hufe devproof.org
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
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
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
	private boolean isAuthor = false;

	public BookmarkBasePage(final PageParameters params) {
		super(params);
		setAuthorRight();
		add(createCSSHeaderContributor());
		add(createHiddenModalWindow());
		addBookmarkAddLink();
		addDeadlinkCheckLink();
		addDeliciousSyncLink();
	}

	private HeaderContributor createCSSHeaderContributor() {
		return CSSPackageResource.getHeaderContribution(BookmarkConstants.REF_BOOKMARK_CSS);
	}

	private WebMarkupContainer createHiddenModalWindow() {
		if (isAuthor()) {
			modalWindow = new ModalWindow("modalWindow");
		} else {
			modalWindow = new WebMarkupContainer("modalWindow");
			modalWindow.setVisible(false);
		}
		return modalWindow;
	}

	private void addDeliciousSyncLink() {
		if (isAuthor()) {
			addPageAdminBoxLink(createDeliciousSyncLink());
		}
	}

	private AjaxLink<BookmarkEntity> createDeliciousSyncLink() {
		AjaxLink<BookmarkEntity> syncLink = newDeliciousSyncLink();
		syncLink.add(new Label("linkName", getString("syncLink")));
		return syncLink;
	}

	private AjaxLink<BookmarkEntity> newDeliciousSyncLink() {
		return new AjaxLink<BookmarkEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ModalWindow modalWindow = (ModalWindow) BookmarkBasePage.this.modalWindow;
				DeliciousSyncPanel syncPanel = createDeliciousSyncPanel(modalWindow.getContentId());
				modalWindow.setInitialHeight(600);
				modalWindow.setInitialWidth(800);
				modalWindow.setContent(syncPanel);
				modalWindow.show(target);
			}

			private DeliciousSyncPanel createDeliciousSyncPanel(String id) {
				return new DeliciousSyncPanel(id);
			}
		};
	}

	private void addDeadlinkCheckLink() {
		if (isAuthor()) {
			addPageAdminBoxLink(createDeadlinkCheckLink());
		}
	}

	private void addBookmarkAddLink() {
		if (isAuthor()) {
			addPageAdminBoxLink(createBookmarkAddLink());
		}
	}

	private Link<?> createBookmarkAddLink() {
		Link<?> addLink = newBookmarkAddLink();
		addLink.add(createAddLinkLabel());
		return addLink;
	}

	private Label createAddLinkLabel() {
		return new Label("linkName", getString("createLink"));
	}

	private Link<?> newBookmarkAddLink() {
		return new Link<Object>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				BookmarkEntity newBookmark = bookmarkService.newBookmarkEntity();
				IModel<BookmarkEntity> bookmarkModel = Model.of(newBookmark);
				setResponsePage(new BookmarkEditPage(bookmarkModel));
			}
		};
	}

	private AjaxLink<BookmarkEntity> createDeadlinkCheckLink() {
		AjaxLink<BookmarkEntity> deadlinkCheckLink = newDeadlinkCheckLink();
		deadlinkCheckLink.add(createDeadlinkCheckLabel());
		return deadlinkCheckLink;
	}

	private Label createDeadlinkCheckLabel() {
		return new Label("linkName", getString("deadlinkCheckLink"));
	}

	private AjaxLink<BookmarkEntity> newDeadlinkCheckLink() {
		return new AjaxLink<BookmarkEntity>("adminLink") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				ModalWindow modalWindow = (ModalWindow) BookmarkBasePage.this.modalWindow;
				DeadlinkCheckPanel<BookmarkEntity> deadlinkCheckPanel = createDeadlinkCheckPanel(modalWindow
						.getContentId());
				modalWindow.setInitialHeight(300);
				modalWindow.setInitialWidth(500);
				modalWindow.setContent(deadlinkCheckPanel);
				modalWindow.show(target);
			}

			private DeadlinkCheckPanel<BookmarkEntity> createDeadlinkCheckPanel(String id) {
				List<BookmarkEntity> allBookmarks = bookmarkService.findAll();
				return newDeadlinkCheckPanel(id, allBookmarks);
			}

			private DeadlinkCheckPanel<BookmarkEntity> newDeadlinkCheckPanel(String id,
					List<BookmarkEntity> allBookmarks) {
				return new DeadlinkCheckPanel<BookmarkEntity>(id, "bookmark", allBookmarks) {
					private static final long serialVersionUID = 1L;

					@Override
					public void onBroken(BookmarkEntity brokenEntity) {
						bookmarkService.markBrokenBookmark(brokenEntity);
					}

					@Override
					public void onValid(BookmarkEntity validEntity) {
						bookmarkService.markValidBookmark(validEntity);
					}
				};
			}
		};
	}

	private void setAuthorRight() {
		PortalSession session = (PortalSession) getSession();
		isAuthor = session.hasRight("page.BookmarkEditPage");
	}

	public WebMarkupContainer getModalWindow() {
		return modalWindow;
	}

	public boolean isAuthor() {
		return isAuthor;
	}
}
