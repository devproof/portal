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

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;
import org.devproof.portal.core.module.role.service.RoleService;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Carsten Hufe
 */
public abstract class BaseFeedPage extends WebPage {
    @SpringBean(name = "feedProviderRegistry")
    private FeedProviderRegistry feedProviderRegistry;
    @SpringBean(name = "roleService")
    private RoleService roleService;
    private PageParameters params;
    private String path;

    public BaseFeedPage(PageParameters params) {
        super(params);
        this.params = params;
        this.path = getFeedPath();
    }

    private String getFeedPath() {
        if (params.size() > 0) {
            return params.getString("0");
        }
        return "";
    }

    @Override
    protected final void onRender(MarkupStream markupStream) {
        getResponse().setContentType(getContentType());
        PrintWriter writer = new PrintWriter(getResponse().getOutputStream());
        SyndFeedOutput output = new SyndFeedOutput();
        try {
            SyndFeed feed = createAppropriateFeedProvider();
            output.output(feed, writer);
            writer.close();
        } catch (IOException e) {
            throw new UnhandledException("Error streaming feed.", e);
        } catch (FeedException e) {
            throw new UnhandledException("Error streaming feed.", e);
        }
    }

    private SyndFeed createAppropriateFeedProvider() {
        FeedProvider feedProvider = feedProviderRegistry.getFeedProviderByPath(path);
        final SyndFeed feed;
        if (feedProvider != null) {
            feed = feedProvider.getFeed(getRequestCycle());
        } else {
            feed = new SyndFeedImpl();
        }
        feed.setFeedType(getFeedType());
        return feed;
    }

    protected abstract String getContentType();

    protected abstract String getFeedType();

    @Override
    public String getMarkupType() {
        return "xml";
    }
}
