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
package org.devproof.portal.module.article.service;

import com.sun.syndication.feed.synd.*;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.PageParameters;
import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.SortableQueryDataProvider;
import org.devproof.portal.core.module.common.page.TemplatePage;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.feed.provider.FeedProvider;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.page.ArticlePage;
import org.devproof.portal.module.article.page.ArticleReadPage;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Carsten Hufe
 */
public class ArticleFeedProviderImpl implements FeedProvider {
    private SortableQueryDataProvider<ArticleEntity, ArticleQuery> articleDataProvider;
    private ConfigurationService configurationService;

    @Override
    public SyndFeed getFeed(RequestCycle rc) {
        SyndFeed feed = generateFeed(rc);
        Iterator<? extends ArticleEntity> iterator = getArticleEntries();
        List<SyndEntry> entries = generateFeedEntries(rc, iterator);
        feed.setEntries(entries);
        return feed;
    }

    protected Iterator<? extends ArticleEntity> getArticleEntries() {
        Integer maxNumber = configurationService.findAsInteger(ArticleConstants.CONF_ARTICLE_ENTRIES_IN_FEED);
        Iterator<? extends ArticleEntity> iterator = articleDataProvider.iterator(0, maxNumber);
        return iterator;
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
        return rc.urlFor(ArticlePage.class, new PageParameters()).toString();
    }

    protected List<SyndEntry> generateFeedEntries(RequestCycle rc, Iterator<? extends ArticleEntity> iterator) {
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        while (iterator.hasNext()) {
            ArticleEntity articleEntity = iterator.next();
            SyndEntry entry = new SyndEntryImpl();
            entry.setTitle(articleEntity.getTitle());
            entry.setLink(getUrl(rc, articleEntity));
            entry.setPublishedDate(articleEntity.getModifiedAt());
            entry.setAuthor(articleEntity.getModifiedBy());
            String content = articleEntity.getTeaser();
            content = content != null ? content : "";
            content = content.replaceAll("<(.|\n)*?>", "");
            SyndContent description = new SyndContentImpl();
            description.setType("text/plain");
            description.setValue(StringUtils.abbreviate(content, 200));
            entry.setDescription(description);
            entries.add(entry);
        }
        return entries;
    }

    protected String getUrl(RequestCycle rc, ArticleEntity ArticleEntity) {
        return rc.urlFor(ArticlePage.class, new PageParameters("id=" + ArticleEntity.getId())).toString();
    }

    @Override
    public List<Class<? extends TemplatePage>> getSupportedFeedPages() {
        List<Class<? extends TemplatePage>> pages = new ArrayList<Class<? extends TemplatePage>>();
        pages.add(ArticlePage.class);
        pages.add(ArticleReadPage.class);
        return pages;
    }

    @Override
    public String getFeedName() {
        String pageTitle = configurationService.findAsString(CommonConstants.CONF_PAGE_TITLE);
        String feedName = configurationService.findAsString(ArticleConstants.CONF_ARTICLE_FEED_TITLE);
        return pageTitle + " - " + feedName;
    }

    @Required
    public void setArticleDataProvider(SortableQueryDataProvider<ArticleEntity, ArticleQuery> articleDataProvider) {
        this.articleDataProvider = articleDataProvider;
    }

    @Required
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
