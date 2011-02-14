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
package org.devproof.portal.module.bookmark.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.common.panel.BubblePanel;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.panel.DeliciousSyncPanel;
import org.devproof.portal.module.bookmark.service.BookmarkService;
import org.devproof.portal.module.deadlinkcheck.panel.DeadlinkCheckPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public abstract class BookmarkBasePage extends TemplatePage {

    private static final long serialVersionUID = 1L;
    @SpringBean(name = "bookmarkService")
    private BookmarkService bookmarkService;

    private WebMarkupContainer bubblePanel;

    public BookmarkBasePage(final PageParameters params) {
        super(params);
        add(createCSSHeaderContributor());
        add(createHiddenBubblePanel());
    }

    @Override
    protected List<Component> newPageAdminBoxLinks(String linkMarkupId, String labelMarkupId) {
        if(isAuthor()) {
            List<Component> links = new ArrayList<Component>();
            links.add(createBookmarkAddLink(linkMarkupId, labelMarkupId));
            links.add(createDeadlinkCheckLink(linkMarkupId, labelMarkupId));
            links.add(createDeliciousSyncLink(linkMarkupId, labelMarkupId));
            return links;
        }
        return super.newPageAdminBoxLinks(linkMarkupId, labelMarkupId);
    }

    private HeaderContributor createCSSHeaderContributor() {
        return CSSPackageResource.getHeaderContribution(BookmarkConstants.REF_BOOKMARK_CSS);
    }

    // TODO remove or reuse from parent
    private WebMarkupContainer createHiddenBubblePanel() {
        if (isAuthor()) {
            bubblePanel = new BubblePanel("bubbleWindow");
        } else {
            bubblePanel = new WebMarkupContainer("bubbleWindow");
            bubblePanel.setVisible(false);
        }
        return bubblePanel;
    }

    private AjaxLink<Bookmark> createDeliciousSyncLink(String linkMarkupId, String labelMarkupId) {
        AjaxLink<Bookmark> syncLink = newDeliciousSyncLink(linkMarkupId);
        syncLink.add(new Label(labelMarkupId, getString("syncLink")));
        return syncLink;
    }

    private AjaxLink<Bookmark> newDeliciousSyncLink(String linkMarkupId) {
        return new AjaxLink<Bookmark>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                BubblePanel panel = (BubblePanel) bubblePanel;
                DeliciousSyncPanel syncPanel = createDeliciousSyncPanel(panel.getContentId());
                panel.setContent(syncPanel);
                panel.showModal(target);
            }

            private DeliciousSyncPanel createDeliciousSyncPanel(String id) {
                return new DeliciousSyncPanel(id) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        BubblePanel panel = (BubblePanel) bubblePanel;
                        panel.hide(target);
                        setResponsePage(BookmarkPage.class);
                    }
                };
            }
        };
    }


    private Link<?> createBookmarkAddLink(String linkMarkupId, String labelMarkupId) {
        Link<?> addLink = newBookmarkAddLink(linkMarkupId);
        addLink.add(createAddLinkLabel(labelMarkupId));
        return addLink;
    }

    private Label createAddLinkLabel(String labelMarkupId) {
        return new Label(labelMarkupId, getString("createLink"));
    }

    private Link<?> newBookmarkAddLink(String linkMarkupId) {
        return new Link<Void>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick() {
                Bookmark newBookmark = bookmarkService.newBookmarkEntity();
                IModel<Bookmark> bookmarkModel = Model.of(newBookmark);
                setResponsePage(new BookmarkEditPage(bookmarkModel));
            }
        };
    }

    private AjaxLink<Bookmark> createDeadlinkCheckLink(String linkMarkupId, String labelMarkupId) {
        AjaxLink<Bookmark> deadlinkCheckLink = newDeadlinkCheckLink(linkMarkupId);
        deadlinkCheckLink.add(createDeadlinkCheckLabel(labelMarkupId));
        return deadlinkCheckLink;
    }

    private Label createDeadlinkCheckLabel(String labelMarkupId) {
        return new Label(labelMarkupId, getString("deadlinkCheckLink"));
    }

    private AjaxLink<Bookmark> newDeadlinkCheckLink(String linkMarkupId) {
        return new AjaxLink<Bookmark>(linkMarkupId) {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                BubblePanel panel = (BubblePanel) bubblePanel;
                DeadlinkCheckPanel<Bookmark> deadlinkCheckPanel = createDeadlinkCheckPanel(panel.getContentId());
                panel.setContent(deadlinkCheckPanel);
                panel.showModal(target);
            }

            private DeadlinkCheckPanel<Bookmark> createDeadlinkCheckPanel(String id) {
                IModel<List<Bookmark>> allBookmarksModel = createAllBookmarksModel();
                return new DeadlinkCheckPanel<Bookmark>(id, "bookmark", allBookmarksModel) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void onBroken(Bookmark broken) {
                        bookmarkService.markBrokenBookmark(broken);
                    }

                    @Override
                    public void onValid(Bookmark valid) {
                        bookmarkService.markValidBookmark(valid);
                    }

                    @Override
                    public void onCancel(AjaxRequestTarget target) {
                        BubblePanel panel = (BubblePanel) bubblePanel;
                        panel.hide(target);
                        setResponsePage(BookmarkPage.class);
                    }
                };
            }
        };
    }

    private IModel<List<Bookmark>> createAllBookmarksModel() {
        return new LoadableDetachableModel<List<Bookmark>>() {
            private static final long serialVersionUID = 4970818389582121112L;

            @Override
            protected List<Bookmark> load() {
                return bookmarkService.findAll();
            }
        };
    }

    public boolean isAuthor() {
        PortalSession session = (PortalSession) getSession();
        return session.hasRight(BookmarkConstants.AUTHOR_RIGHT);
    }
}
