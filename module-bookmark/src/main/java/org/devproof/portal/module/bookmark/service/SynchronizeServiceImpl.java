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
package org.devproof.portal.module.bookmark.service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devproof.portal.core.module.tag.TagConstants;
import org.devproof.portal.module.bookmark.BookmarkConstants;
import org.devproof.portal.module.bookmark.bean.DeliciousBean;
import org.devproof.portal.module.bookmark.bean.DeliciousPostBean;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity;
import org.devproof.portal.module.bookmark.entity.BookmarkEntity.Source;
import org.devproof.portal.module.bookmark.entity.BookmarkTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Synchronization methods e.g. for del.icio.us
 *
 * @author Carsten Hufe
 */
@Service("synchronizeService")
public class SynchronizeServiceImpl implements SynchronizeService {
    private final Log logger = LogFactory.getLog(SynchronizeServiceImpl.class);

    private BookmarkService bookmarkService;
    private BookmarkTagService bookmarkTagService;

    @Override
    public DeliciousBean getDataFromDelicious(String username, String password, String tags) {
        logger.debug("Retrieve data from delicious");
        HttpClient httpClient = new HttpClient();
        HttpClientParams httpClientParams = new HttpClientParams();
        DefaultHttpMethodRetryHandler defaultHttpMethodRetryHandler = new DefaultHttpMethodRetryHandler(0, false);
        httpClientParams.setParameter("User-Agent", BookmarkConstants.USER_AGENT);
        httpClientParams.setParameter(HttpClientParams.RETRY_HANDLER, defaultHttpMethodRetryHandler);
        httpClient.setParams(httpClientParams);
        httpClient.getState().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        String urlTag = "";
        if (StringUtils.isNotEmpty(tags)) {
            urlTag = "tag=" + tags;
        }
        HttpMethod method = new GetMethod(BookmarkConstants.DELICIOUS_API + urlTag);
        method.setDoAuthentication(true);
        DeliciousBean bean = new DeliciousBean();
        try {
            int httpCode = httpClient.executeMethod(method);
            bean.setHttpCode(httpCode);
            if (!bean.hasError()) {
                XStream xstream = new XStream(new DomDriver());
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
        } catch (HttpException e) {
            bean.setErrorMessage(e.getMessage());
        } catch (IOException e) {
            bean.setErrorMessage(e.getMessage());
        }
        method.releaseConnection();
        return bean;
    }

    @Override
    public List<BookmarkEntity> getModifiedDeliciousBookmarks(DeliciousBean bean) {
        logger.debug("Retrieve modified data from delicious");
        List<BookmarkEntity> bookmarks = bookmarkService.findBookmarksBySource(Source.DELICIOUS);
        List<BookmarkEntity> back = new ArrayList<BookmarkEntity>(bean.getPosts().size());
        for (DeliciousPostBean post : bean.getPosts()) {
            for (BookmarkEntity bookmark : bookmarks) {
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
    public List<BookmarkEntity> getNewDeliciousBookmarks(DeliciousBean bean) {
        logger.debug("Retrieve new data from delicious");
        List<BookmarkEntity> bookmarks = bookmarkService.findAll();
        List<BookmarkEntity> back = new ArrayList<BookmarkEntity>(bean.getPosts().size());

        for (DeliciousPostBean post : bean.getPosts()) {
            boolean found = false;
            for (BookmarkEntity bookmark : bookmarks) {
                if (post.getHref().equals(bookmark.getUrl()) || post.getHash().equals(bookmark.getSyncHash())) {
                    found = true;
                    break;
                }
            }
            if (!found && post.getHref() != null) {
                BookmarkEntity newBookmark = bookmarkService.newBookmarkEntity();
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

    private List<BookmarkTagEntity> getTagsFromString(String tags) {
        StringTokenizer tokenizer = new StringTokenizer(tags, TagConstants.TAG_SEPERATORS, false);
        List<BookmarkTagEntity> newTags = new ArrayList<BookmarkTagEntity>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            BookmarkTagEntity tag = bookmarkTagService.newTagEntity(token);
            newTags.add(tag);
        }
        return newTags;
    }

    @Override
    public List<BookmarkEntity> getRemovedDeliciousBookmarks(DeliciousBean bean) {
        logger.debug("Retrieve removed data from delicious");
        List<BookmarkTagEntity> searchTags = getTagsFromString(bean.getTag());
        List<BookmarkEntity> bookmarks = bookmarkService.findBookmarksBySource(Source.DELICIOUS);
        List<BookmarkEntity> back = new ArrayList<BookmarkEntity>(bean.getPosts().size());
        for (BookmarkEntity bookmark : bookmarks) {
            boolean found = false;
            for (DeliciousPostBean post : bean.getPosts()) {
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

    private boolean containsTag(Collection<BookmarkTagEntity> org, Collection<BookmarkTagEntity> search) {
        if (search.size() == 0) {
            return true;
        }
        for (BookmarkTagEntity tag : search) {
            if (org.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    @Autowired
    public void setBookmarkService(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @Autowired
    public void setBookmarkTagService(BookmarkTagService bookmarkTagService) {
        this.bookmarkTagService = bookmarkTagService;
    }
}
