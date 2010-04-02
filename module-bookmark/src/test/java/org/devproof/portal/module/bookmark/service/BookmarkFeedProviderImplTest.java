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
package org.devproof.portal.module.bookmark.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.SortableQueryDataProvider;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.page.BookmarkPage;
import org.devproof.portal.module.bookmark.query.BookmarkQuery;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * @author Carsten Hufe
 */
public class BookmarkFeedProviderImplTest extends TestCase {
	private BookmarkFeedProviderImpl impl;
	private SortableQueryDataProvider<BookmarkEntity, BookmarkQuery> dataProviderMock;
	private ConfigurationService configurationServiceMock;

	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		dataProviderMock = createStrictMock(SortableQueryDataProvider.class);
		configurationServiceMock = createMock(ConfigurationService.class);
		impl = new BookmarkFeedProviderImpl() {
			@Override
			protected String getUrl(RequestCycle rc) {
				return "http://url";
			}

			@Override
			protected String getUrl(RequestCycle rc, BookmarkEntity bookmarkEntity) {
				return "http://url/" + bookmarkEntity.getId();
			}
		};
		impl.setConfigurationService(configurationServiceMock);
		impl.setBookmarkDataProvider(dataProviderMock);
	}

	public void testGetFeedName() {
		expect(configurationServiceMock.findAsString(CommonConstants.CONF_PAGE_TITLE)).andReturn("pagetitle");
		expect(configurationServiceMock.findAsString(BookmarkConstants.CONF_BOOKMARK_FEED_TITLE))
				.andReturn("feedtitle");
		replay(configurationServiceMock);
		assertEquals("pagetitle - feedtitle", impl.getFeedName());
		verify(configurationServiceMock);
	}

	public void testSupportedPages() {
		assertEquals(BookmarkPage.class, impl.getSupportedFeedPages().get(0));
	}

	@SuppressWarnings("unchecked")
	public void testGetBookmarkEntries() {
		BookmarkEntity bookmark = createBookmark();
		Iterator it = Arrays.asList(bookmark).iterator();
		expect(configurationServiceMock.findAsInteger(BookmarkConstants.CONF_BOOKMARK_ENTRIES_IN_FEED)).andReturn(10);
		expect(dataProviderMock.iterator(0, 10)).andReturn(it);
		replay(configurationServiceMock);
		replay(dataProviderMock);
		Iterator<? extends BookmarkEntity> bookmarkEntries = impl.getBookmarkEntries();
		assertSame(bookmarkEntries, it);
		verify(configurationServiceMock);
		verify(dataProviderMock);
	}

	public void testGenerateFeed() {
		expect(configurationServiceMock.findAsString(CommonConstants.CONF_PAGE_TITLE)).andReturn("pagetitle")
				.anyTimes();
		expect(configurationServiceMock.findAsString(BookmarkConstants.CONF_BOOKMARK_FEED_TITLE))
				.andReturn("feedtitle").anyTimes();
		replay(configurationServiceMock);
		SyndFeed feed = impl.generateFeed(null);
		assertEquals("pagetitle - feedtitle", feed.getTitle());
		assertEquals("pagetitle - feedtitle", feed.getDescription());
		assertEquals("http://url", feed.getLink());
		verify(configurationServiceMock);
	}

	@SuppressWarnings("unchecked")
	public void testGenerateFeedEntries() {
		BookmarkEntity bookmark = createBookmark();
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
		impl = new BookmarkFeedProviderImpl() {
			@Override
			protected SyndFeed generateFeed(RequestCycle rc) {
				callOrder.append("1");
				return new SyndFeedImpl();
			}

			@Override
			protected Iterator<? extends BookmarkEntity> getBookmarkEntries() {
				callOrder.append("2");
				return null;
			}

			@Override
			protected List<SyndEntry> generateFeedEntries(RequestCycle rc, Iterator<? extends BookmarkEntity> iterator) {
				callOrder.append("3");
				return entries;
			}

			@Override
			protected String getUrl(RequestCycle rc) {
				return "";
			}
		};
		impl.getFeed(null, new RoleEntity());
		assertEquals("123", callOrder.toString());
	}

	private BookmarkEntity createBookmark() {
		BookmarkEntity bookmark = new BookmarkEntity();
		bookmark.setId(1);
		bookmark.setTitle("hello");
		bookmark.setDescription("world");
		bookmark.setModifiedBy("maxpower");
		bookmark.setModifiedAt(new Date());
		return bookmark;
	}
}
