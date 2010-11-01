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
package org.devproof.portal.core.module.feed.page;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.config.ModulePage;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Carsten Hufe
 */
@ModulePage(mountPath = "/feeds")
public class FeedOverviewPage extends TemplatePage {
    private static final long serialVersionUID = 3957452424603515088L;
    @SpringBean(name = "feedProviderRegistry")
    private FeedProviderRegistry feedProviderRegistry;
    private Map<String, FeedProvider> allFeedProvider;

    public FeedOverviewPage(PageParameters params) {
        super(params);
        allFeedProvider = feedProviderRegistry.getAllFeedProvider();
        add(createRepeatingFeeds());
    }

    private ListView<String> createRepeatingFeeds() {
        IModel<List<String>> pathModel = createPathModel();
        return new ListView<String>("repeatingFeeds", pathModel) {
            private static final long serialVersionUID = 6289409135117578201L;

            @Override
            protected void populateItem(ListItem<String> item) {
                String path = item.getModelObject();
                FeedProvider provider = allFeedProvider.get(path);
                item.add(createFeedNameLabel(provider));
                item.add(createPathLabel(path));
                item.add(createSupportedPagesLabel(provider));
                item.add(createAtom1Link(path));
                item.add(createRss2Link(path));
            }
        };
    }

    private IModel<List<String>> createPathModel() {
        return new LoadableDetachableModel<List<String>>() {
            private static final long serialVersionUID = -2147576128407507758L;

            @Override
            protected List<String> load() {
                return new ArrayList<String>(allFeedProvider.keySet());
            }
        };
    }


    private BookmarkablePageLink<Rss2FeedPage> createRss2Link(String path) {
        return new BookmarkablePageLink<Rss2FeedPage>("rss2Link", Rss2FeedPage.class, new PageParameters("0=" + path));
    }

    private BookmarkablePageLink<Atom1FeedPage> createAtom1Link(String path) {
        return new BookmarkablePageLink<Atom1FeedPage>("atom1Link", Atom1FeedPage.class, new PageParameters("0=" + path));
    }

    private Component createSupportedPagesLabel(FeedProvider provider) {
        Label supportedPages = new Label("pages", getSupportedPagesString(provider));
        supportedPages.setEscapeModelStrings(false);
        return supportedPages;
    }

    private Label createPathLabel(String path) {
        return new Label("path", path);
    }

    private Label createFeedNameLabel(FeedProvider provider) {
        IModel<String> feedNameModel = new PropertyModel<String>(provider, "feedName");
        return new Label("feedName", feedNameModel);
    }

    private String getSupportedPagesString(FeedProvider provider) {
        StringBuilder buf = new StringBuilder();
        for (Class<? extends TemplatePage> page : provider.getSupportedFeedPages()) {
            buf.append(page.getSimpleName()).append(", ");
        }
        return buf.toString();
    }
}
