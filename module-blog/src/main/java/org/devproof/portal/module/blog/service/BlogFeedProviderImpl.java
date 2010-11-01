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
package org.devproof.portal.module.blog.service;

import com.sun.syndication.feed.synd.*;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.SortableQueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.page.BlogPage;
import org.devproof.portal.module.blog.query.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Component
public class BlogFeedProviderImpl implements FeedProvider {
    private SortableQueryDataProvider<BlogEntity, BlogQuery> blogDataProvider;
    private ConfigurationService configurationService;

    @Override
    public SyndFeed getFeed(RequestCycle rc) {
        SyndFeed feed = generateFeed(rc);
        Iterator<? extends BlogEntity> iterator = getBlogEntries();
        List<SyndEntry> entries = generateFeedEntries(rc, iterator);
        feed.setEntries(entries);
        return feed;
    }

    protected Iterator<? extends BlogEntity> getBlogEntries() {
        Integer maxNumber = configurationService.findAsInteger(BlogConstants.CONF_BLOG_ENTRIES_IN_FEED);
        return blogDataProvider.iterator(0, maxNumber);
    }

    protected SyndFeed generateFeed(RequestCycle rc) {
        SyndFeed feed = new SyndFeedImpl();
        feed.setTitle(getFeedName());
        feed.setLink(getUrl(rc));
        String pageTitle = configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE);
        feed.setAuthor(pageTitle);
        feed.setCopyright(pageTitle);
        // must be set for RSS2 feed
        feed.setDescription(getFeedName());
        return feed;
    }

    protected String getUrl(RequestCycle rc) {
        return rc.urlFor(BlogPage.class, new PageParameters()).toString();
    }

    protected List<SyndEntry> generateFeedEntries(RequestCycle rc, Iterator<? extends BlogEntity> iterator) {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        while (iterator.hasNext()) {
            BlogEntity blogEntity = iterator.next();
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(blogEntity.getHeadline());
            entry.setLink(getUrl(rc, blogEntity));
            entry.setPublishedDate(blogEntity.getModifiedAt());
            entry.setAuthor(blogEntity.getModifiedBy());
            String content = blogEntity.getContent().replaceAll("<(.|\n)*?>", "");
            SyndContent description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(StringUtils.abbreviate(content, 200));
            entry.setDescription(description);
            entries.add(entry);
        }
        return entries;
    }

    protected String getUrl(RequestCycle rc, BlogEntity blogEntity) {
        return rc.urlFor(BlogPage.class, new PageParameters("id=" + blogEntity.getId())).toString();
    }

    @Override
    public List<Class<? extends TemplatePage>> getSupportedFeedPages() {
        List<Class<? extends TemplatePage>> pages = new ArrayList<Class<? extends TemplatePage>>();
        pages.add(BlogPage.class);
        return pages;
    }

    @Override
    public String getFeedName() {
        String pageTitle = configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE);
        String feedName = configurationService.findAsString(BlogConstants.CONF_BLOG_FEED_TITLE);
        return pageTitle + " - " + feedName;
    }

    @Autowired
    public void setBlogDataProvider(SortableQueryDataProvider<BlogEntity, BlogQuery> blogDataProvider) {
        this.blogDataProvider = blogDataProvider;
    }

    @Autowired
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
