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
package org.devproof.portal.core.module.feed.page;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;
import org.apache.commons.lang.UnhandledException;
import org.apache.wicket.Component;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.core.module.feed.registry.FeedProviderRegistry;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Carsten Hufe
 */
public abstract class BaseFeedPage extends WebPage {
    private static final long serialVersionUID = -2301541781636426434L;

    @SpringBean(name = "feedProviderRegistry")
    private FeedProviderRegistry feedProviderRegistry;
    private PageParameters params;
    private String path;

    public BaseFeedPage(PageParameters params) {
        super(params);
        this.params = params;
        this.path = getFeedPath();
    }

    private String getFeedPath() {
        return params.get("module").toString("");
    }

    @Override
    public IMarkupFragment getMarkup() {
        return Markup.of("<xmlfeed/>");
    }

    @Override
    protected void onRender() {
        SyndFeedOutput output = new SyndFeedOutput();
        try {
            SyndFeed feed = createAppropriateFeedProvider();
            String feedXml = output.outputString(feed);
            getResponse().write(feedXml);
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
    public MarkupType getMarkupType() {
        return new MarkupType("xml", getContentType());
    }
}
