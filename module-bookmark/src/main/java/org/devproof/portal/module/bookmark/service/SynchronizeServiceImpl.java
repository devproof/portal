/*
 * Copyright 2009 Carsten Hufe devproof.org
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.core.module.tag.service.TagService;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.bean.DeliciousBean;
import org.devproof.portal.module.bookmark.bean.DeliciousPostBean;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Synchronization methods e.g. for del.icio.us
 * 
 * @author Carsten Hufe
 * 
 */
public class SynchronizeServiceImpl implements SynchronizeService {
	private static final Log LOG = LogFactory.getLog(SynchronizeServiceImpl.class);

	private BookmarkService bookmarkService;
	private TagService<BookmarkTagEntity> tagService;

	@Override
	public DeliciousBean getDataFromDelicious(final String username, final String password, final String tags) {
		LOG.debug("Retrieve data from delicious");
		final HttpClient httpClient = new HttpClient();
		final HttpClientParams httpClientParams = new HttpClientParams();
		final DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(0, false);
		httpClientParams.setParameter("User-Agent", BookmarkConstants.USER_AGENT);
		httpClientParams.setParameter(HttpClientParams.RETRY_HANDLER, defaultHttpMethodRetryHandler);
		httpClient.setParams(httpClientParams);
		httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
		String urlTag = "";
		if (StringUtils.isNotEmpty(tags)) {
			urlTag = "tag=" + tags;
		}
		final HttpMethod method = new GetMethod(BookmarkConstants.DELICIOUS_API + urlTag);
		method.setDoAuthentication(true);
		DeliciousBean bean = new DeliciousBean();
		try {
			final int httpCode = httpClient.executeMethod(method);
			bean.setHttpCode(httpCode);
			if (!bean.hasError()) {
				final XStream xstream = new XStream(new DomDriver());
				xstream.alias("posts", DeliciousBean.class);
				xstream.alias("post", DeliciousPostBean.class);
				xstream.addImplicitCollection(DeliciousBean.class, "posts");
				xstream.useAttributeFor(String.class);
				xstream.useAttributeFor(Integer.class);
				bean = (DeliciousBean) xstream.fromXML(method.getResponseBodyAsStream());
				bean.setHttpCode(httpCode);
			} else {
				bean.setErrorMessage("Unknown Error: Http Status: " + httpCode);
			}
		} catch (final HttpException e) {
			bean.setErrorMessage(e.getMessage());
		} catch (final IOException e) {
			bean.setErrorMessage(e.getMessage());
		}
		method.releaseConnection();
		return bean;
	}

	@Override
	public List<BookmarkEntity> getModifiedDeliciousBookmarks(final DeliciousBean bean) {
		LOG.debug("Retrieve modified data from delicious");
		final List<BookmarkEntity> bookmarks = bookmarkService.findBookmarksBySource(Source.DELICIOUS);
		final List<BookmarkEntity> back = new ArrayList<BookmarkEntity>(bean.getPosts().size());
		for (final DeliciousPostBean post : bean.getPosts()) {
			for (final BookmarkEntity bookmark : bookmarks) {
				if (post.getHash().equals(bookmark.getSyncHash())) {
					bookmark.setSource(Source.DELICIOUS);
					bookmark.setTitle(post.getDescription());
					bookmark.setSyncUsername(bean.getUser());
					bookmark.setSyncHash(post.getHash());
					bookmark.setUrl(post.getHref());
					bookmark.setBroken(Boolean.FALSE);
					if (StringUtils.isEmpty(post.getExtended())) {
						bookmark.setDescription("<p>" + post.getDescription() + "</p>");
					} else {
						bookmark.setDescription("<p>" + post.getExtended() + "</p>");
					}
					bookmark.setTags(getTagsFromString(post.getTag()));
					back.add(bookmark);
					break;
				}
			}
		}
		return back;
	}

	@Override
	public List<BookmarkEntity> getNewDeliciousBookmarks(final DeliciousBean bean) {
		LOG.debug("Retrieve new data from delicious");
		final List<BookmarkEntity> bookmarks = bookmarkService.findAll();
		final List<BookmarkEntity> back = new ArrayList<BookmarkEntity>(bean.getPosts().size());

		for (final DeliciousPostBean post : bean.getPosts()) {
			boolean found = false;
			for (final BookmarkEntity bookmark : bookmarks) {
				if (post.getHref().equals(bookmark.getUrl()) || post.getHash().equals(bookmark.getSyncHash())) {
					found = true;
					break;
				}
			}
			if (!found && post.getHref() != null) {
				final BookmarkEntity newBookmark = bookmarkService.newBookmarkEntity();
				newBookmark.setSource(Source.DELICIOUS);
				newBookmark.setTitle(post.getDescription());
				newBookmark.setSyncUsername(bean.getUser());
				newBookmark.setSyncHash(post.getHash());
				newBookmark.setUrl(post.getHref());
				if (StringUtils.isEmpty(post.getExtended())) {
					newBookmark.setDescription("<p>" + post.getDescription() + "</p>");
				} else {
					newBookmark.setDescription("<p>" + post.getExtended() + "</p>");
				}
				newBookmark.setTags(getTagsFromString(post.getTag()));
				back.add(newBookmark);
			}
		}
		return back;
	}

	private List<BookmarkTagEntity> getTagsFromString(final String tags) {
		final StringTokenizer tokenizer = new StringTokenizer(tags, TagConstants.TAG_SEPERATORS, false);
		final List<BookmarkTagEntity> newTags = new ArrayList<BookmarkTagEntity>(tokenizer.countTokens());
		while (tokenizer.hasMoreTokens()) {
			final String token = tokenizer.nextToken().trim();
			final BookmarkTagEntity tag = tagService.newTagEntity(token);
			newTags.add(tag);
		}
		return newTags;
	}

	@Override
	public List<BookmarkEntity> getRemovedDeliciousBookmarks(final DeliciousBean bean) {
		LOG.debug("Retrieve removed data from delicious");
		final List<BookmarkTagEntity> searchTags = getTagsFromString(bean.getTag());
		final List<BookmarkEntity> bookmarks = bookmarkService.findBookmarksBySource(Source.DELICIOUS);
		final List<BookmarkEntity> back = new ArrayList<BookmarkEntity>(bean.getPosts().size());
		for (final BookmarkEntity bookmark : bookmarks) {
			boolean found = false;
			for (final DeliciousPostBean post : bean.getPosts()) {
				if (post.getHash().equals(bookmark.getSyncHash())) {
					found = true;
					break;
				}
			}
			if (!found && containsTag(bookmark.getTags(), searchTags)) {
				back.add(bookmark);
			}
		}
		return back;
	}

	private boolean containsTag(final Collection<BookmarkTagEntity> org, final Collection<BookmarkTagEntity> search) {
		if (search.size() == 0) {
			return true;
		}
		for (final BookmarkTagEntity tag : search) {
			if (org.contains(tag)) {
				return true;
			}
		}
		return false;
	}

	public void setBookmarkService(final BookmarkService bookmarkService) {
		this.bookmarkService = bookmarkService;
	}

	public void setTagService(final TagService<BookmarkTagEntity> tagService) {
		this.tagService = tagService;
	}
}
