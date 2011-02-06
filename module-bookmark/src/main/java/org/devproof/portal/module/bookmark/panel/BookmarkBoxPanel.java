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
package org.devproof.portal.module.bookmark.panel;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.app.PortalSession;
import org.devproof.portal.core.config.NavigationBox;
import org.devproof.portal.core.module.box.panel.BoxTitleVisibility;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.Bookmark;
import org.devproof.portal.module.bookmark.page.BookmarkPage;
import org.devproof.portal.module.bookmark.service.BookmarkService;

import java.util.List;

/**
 * @author Carsten Hufe
 */
@NavigationBox("Latest Bookmark Box")
public class BookmarkBoxPanel extends Panel implements BoxTitleVisibility {

    private static final long serialVersionUID = 1L;

    @SpringBean(name = "bookmarkService")
    private BookmarkService bookmarkService;
    @SpringBean(name = "configurationService")
    private ConfigurationService configurationService;
    private WebMarkupContainer titleContainer;
    private IModel<List<Bookmark>> latestBookmarksModel;

    public BookmarkBoxPanel(String id) {
        super(id);
        latestBookmarksModel = createLatestBookmarksModel();
        add(createTitleContainer());
        add(createRepeatingBookmarks());
    }

    @Override
    public boolean isVisible() {
        List<Bookmark> latestBookmarks = latestBookmarksModel.getObject();
        return latestBookmarks.size() > 0;
    }

    private ListView<Bookmark> createRepeatingBookmarks() {
        return new ListView<Bookmark>("repeatingBookmarks", latestBookmarksModel) {
            private static final long serialVersionUID = 6603619378248308439L;

            @Override
            protected void populateItem(ListItem<Bookmark> item) {
                item.add(createLinkToBookmark(item.getModel()));
            }
        };
    }

    private BookmarkablePageLink<BookmarkPage> createLinkToBookmark(IModel<Bookmark> bookmarkModel) {
        BookmarkablePageLink<BookmarkPage> link = new BookmarkablePageLink<BookmarkPage>("link", BookmarkPage.class);
        Bookmark bookmark = bookmarkModel.getObject();
        link.setParameter("id", bookmark.getId());
        link.add(createLinkNameLabel(bookmarkModel));
        return link;
    }

    private Label createLinkNameLabel(IModel<Bookmark> bookmarkModel) {
        IModel<String> titleModel = new PropertyModel<String>(bookmarkModel, "title");
        return new Label("linkName", titleModel);
    }

    private IModel<List<Bookmark>> createLatestBookmarksModel() {
        return new LoadableDetachableModel<List<Bookmark>>() {
            private static final long serialVersionUID = 6940753456307593228L;

            @Override
            protected List<Bookmark> load() {
                PortalSession session = (PortalSession) getSession();
                Integer num = configurationService.findAsInteger(BookmarkConstants.CONF_BOX_NUM_LATEST_BOOKMARKS);
                return bookmarkService.findAllBookmarksForRoleOrderedByDateDesc(session.getRole(), 0, num);
            }
        };
    }

    private WebMarkupContainer createTitleContainer() {
        titleContainer = new WebMarkupContainer("title");
        return titleContainer;
    }

    @Override
    public void setTitleVisible(boolean visible) {
        titleContainer.setVisible(visible);
    }
}
