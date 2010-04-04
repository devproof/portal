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
package org.devproof.portal.module.blog.service;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import junit.framework.TestCase;
import org.apache.wicket.RequestCycle;
import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.dataprovider.SortableQueryDataProvider;
import org.devproof.portal.core.module.configuration.service.ConfigurationService;
import org.devproof.portal.core.module.role.entity.RoleEntity;
import org.devproof.portal.module.blog.BlogConstants;
import org.devproof.portal.module.blog.entity.BlogEntity;
import org.devproof.portal.module.blog.page.BlogPage;
import org.devproof.portal.module.blog.query.BlogQuery;

import java.util.*;

import static org.easymock.EasyMock.*;

/**
 * @author Carsten Hufe
 */
public class BlogFeedProviderImplTest extends TestCase {
	private BlogFeedProviderImpl impl;
	private SortableQueryDataProvider<BlogEntity, BlogQuery> dataProviderMock;
	private ConfigurationService configurationServiceMock;

	@Override
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		dataProviderMock = createStrictMock(SortableQueryDataProvider.class);
		configurationServiceMock = createMock(ConfigurationService.class);
		impl = new BlogFeedProviderImpl() {
			@Override
			protected String getUrl(RequestCycle rc) {
				return "http://url";
			}

			@Override
			protected String getUrl(RequestCycle rc, BlogEntity blogEntity) {
				return "http://url/" + blogEntity.getId();
			}
		};
		impl.setConfigurationService(configurationServiceMock);
		impl.setBlogDataProvider(dataProviderMock);
	}

	public void testGetFeedName() {
		expect(configurationServiceMock.findAsString(CommonConstants.CONF_PAGE_TITLE)).andReturn("pagetitle");
		expect(configurationServiceMock.findAsString(BlogConstants.CONF_BLOG_FEED_TITLE)).andReturn("feedtitle");
		replay(configurationServiceMock);
		assertEquals("pagetitle - feedtitle", impl.getFeedName());
		verify(configurationServiceMock);
	}

	public void testSupportedPages() {
		assertEquals(BlogPage.class, impl.getSupportedFeedPages().get(0));
	}

	@SuppressWarnings("unchecked")
	public void testGetBlogEntries() {
		BlogEntity blog = createBlog();
		Iterator it = Arrays.asList(blog).iterator();
		expect(configurationServiceMock.findAsInteger(BlogConstants.CONF_BLOG_ENTRIES_IN_FEED)).andReturn(10);
		expect(dataProviderMock.iterator(0, 10)).andReturn(it);
		replay(configurationServiceMock);
		replay(dataProviderMock);
		Iterator<? extends BlogEntity> blogEntries = impl.getBlogEntries();
		assertSame(blogEntries, it);
		verify(configurationServiceMock);
		verify(dataProviderMock);
	}

	public void testGenerateFeed() {
		expect(configurationServiceMock.findAsString(CommonConstants.CONF_PAGE_TITLE)).andReturn("pagetitle")
				.anyTimes();
		expect(configurationServiceMock.findAsString(BlogConstants.CONF_BLOG_FEED_TITLE)).andReturn("feedtitle")
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
		BlogEntity blog = createBlog();
		Iterator it = Arrays.asList(blog).iterator();
		List<SyndEntry> generateFeedEntries = impl.generateFeedEntries(null, it);
		SyndEntry entry = generateFeedEntries.get(0);
		assertEquals("hello", entry.getTitle());
		assertEquals("http://url/" + blog.getId(), entry.getLink());
		assertEquals("world", entry.getDescription().getValue());
		assertEquals("text/plain", entry.getDescription().getType());
		assertEquals("maxpower", entry.getAuthor());
		assertNotNull(entry.getPublishedDate());
	}

	public void testGetFeed() {
		final List<SyndEntry> entries = new ArrayList<SyndEntry>();
		final StringBuilder callOrder = new StringBuilder();
		impl = new BlogFeedProviderImpl() {
			@Override
			protected SyndFeed generateFeed(RequestCycle rc) {
				callOrder.append("1");
				return new SyndFeedImpl();
			}

			@Override
			protected Iterator<? extends BlogEntity> getBlogEntries() {
				callOrder.append("2");
				return null;
			}

			@Override
			protected List<SyndEntry> generateFeedEntries(RequestCycle rc, Iterator<? extends BlogEntity> iterator) {
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

	private BlogEntity createBlog() {
		BlogEntity blog = new BlogEntity();
		blog.setId(1);
		blog.setHeadline("hello");
		blog.setContent("world");
		blog.setModifiedBy("maxpower");
		blog.setModifiedAt(new Date());
		return blog;
	}
}
