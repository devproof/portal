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

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import junit.framework.TestCase;
import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.SortableQueryDataProvider;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.entity.ArticleEntity;
import org.devproof.portal.module.article.page.ArticlePage;
import org.devproof.portal.module.article.query.ArticleQuery;

import java.util.*;

import static org.easymock.EasyMock.*;

/**
 * @author Carsten Hufe
 */
public class ArticleFeedProviderImplTest extends TestCase {
	private ArticleFeedProviderImpl impl;
	private SortableQueryDataProvider<ArticleEntity, ArticleQuery> dataProviderMock;
	private ConfigurationService configurationServiceMock;

	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		dataProviderMock = createStrictMock(SortableQueryDataProvider.class);
		configurationServiceMock = createMock(ConfigurationService.class);
		impl = new ArticleFeedProviderImpl() {
			@Override
			protected String getUrl(RequestCycle rc) {
				return "http://url";
			}

			@Override
			protected String getUrl(RequestCycle rc, ArticleEntity articleEntity) {
				return "http://url/" + articleEntity.getId();
			}
		};
		impl.setConfigurationService(configurationServiceMock);
		impl.setArticleDataProvider(dataProviderMock);
	}

	public void testGetFeedName() {
		expect(configurationServiceMock.findAsString(CommonConstants.CONF_PAGE_TITLE)).andReturn("pagetitle");
		expect(configurationServiceMock.findAsString(ArticleConstants.CONF_ARTICLE_FEED_TITLE)).andReturn("feedtitle");
		replay(configurationServiceMock);
		assertEquals("pagetitle - feedtitle", impl.getFeedName());
		verify(configurationServiceMock);
	}

	public void testSupportedPages() {
		assertEquals(ArticlePage.class, impl.getSupportedFeedPages().get(0));
	}

	@SuppressWarnings("unchecked")
	public void testGetArticleEntries() {
		ArticleEntity article = createArticle();
		Iterator it = Arrays.asList(article).iterator();
		expect(configurationServiceMock.findAsInteger(ArticleConstants.CONF_ARTICLE_ENTRIES_IN_FEED)).andReturn(10);
		expect(dataProviderMock.iterator(0, 10)).andReturn(it);
		replay(configurationServiceMock);
		replay(dataProviderMock);
		Iterator<? extends ArticleEntity> bookmarkEntries = impl.getArticleEntries();
		assertSame(bookmarkEntries, it);
		verify(configurationServiceMock);
		verify(dataProviderMock);
	}

	public void testGenerateFeed() {
		expect(configurationServiceMock.findAsString(CommonConstants.CONF_PAGE_TITLE)).andReturn("pagetitle")
				.anyTimes();
		expect(configurationServiceMock.findAsString(ArticleConstants.CONF_ARTICLE_FEED_TITLE)).andReturn("feedtitle")
				.anyTimes();
		replay(configurationServiceMock);
		SyndFeed feed = impl.generateFeed(null);
		assertEquals("pagetitle - feedtitle", feed.getTitle());
		assertEquals("pagetitle - feedtitle", feed.getDescription());
		assertEquals("http://url", feed.getLink());
		verify(configurationServiceMock);
	}

	@SuppressWarnings("unchecked")
	public void testGenerateFeedEntries() {
		ArticleEntity bookmark = createArticle();
		Iterator it = Arrays.asList(bookmark).iterator();
		List<SyndEntry> generateFeedEntries = impl.generateFeedEntries(null, it);
		SyndEntry entry = generateFeedEntries.get(0);
		assertEquals("hello", entry.getTitle());
		assertEquals("http://url/" + bookmark.getId(), entry.getLink());
		assertEquals("world", entry.getDescription().getValue());
		assertEquals("text/plain", entry.getDescription().getType());
		assertEquals("maxpower", entry.getAuthor());
		assertNotNull(entry.getPublishedDate());
	}

	public void testGetFeed() {
		final List<SyndEntry> entries = new ArrayList<SyndEntry>();
		final StringBuilder callOrder = new StringBuilder();
		impl = new ArticleFeedProviderImpl() {
			@Override
			protected SyndFeed generateFeed(final RequestCycle rc) {
				callOrder.append("1");
				return new SyndFeedImpl();
			}

			@Override
			protected Iterator<? extends ArticleEntity> getArticleEntries() {
				callOrder.append("2");
				return null;
			}

			@Override
			protected List<SyndEntry> generateFeedEntries(final RequestCycle rc,
					final Iterator<? extends ArticleEntity> iterator) {
				callOrder.append("3");
				return entries;
			}

			@Override
			protected String getUrl(final RequestCycle rc) {
				return "";
			}
		};
		impl.getFeed(null);
		assertEquals("123", callOrder.toString());
	}

	private ArticleEntity createArticle() {
		ArticleEntity article = new ArticleEntity();
		article.setId(1);
		article.setTitle("hello");
		article.setTeaser("world");
		article.setModifiedBy("maxpower");
		article.setModifiedAt(new Date());
		return article;
	}
}
