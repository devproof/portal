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
package org.devproof.portal.core.module.mount.service;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.module.mount.entity.MountPoint;

import java.util.List;

/**
 * Provides services regarding global mount URLs
 *
 * @author Carsten Hufe
 */
public interface MountService {

    /**
     * Builds the request target for a request URL
     *
     * @param requestedUrl requested URL
     * @return wicket request target
     */
    // TODO entfernen?
//    IRequestTarget resolveRequestTarget(String requestedUrl);

    /**
     * Returns the mount point which is marked as defaultUrl
     *
     * @param relatedContentId  releated content id e.g. "3"
     * @param handlerKey handler key e.g. "article"
     * @return matching mount point
     */
    MountPoint findDefaultMountPoint(String relatedContentId, String handlerKey);

    /**
     * Checks if a URL exists
     * @param requestedUrl url to check
     * @return true if the URL exists
     */
    boolean existsPath(String requestedUrl);

    /**
     * Returns the matching URL for the page
     *
     * @param pageClazz page class to map
     * @param params page params
     * @return url
     */
    String urlFor(Class<? extends Page> pageClazz, PageParameters params);

    /**
     * Returns true if there is a mount handler which can manage the page
     *
     * @param pageClazz clazz to check
     * @param pageParameters page params
     * @return true if the page can be handled
     */
    boolean canHandlePageClass(Class<? extends Page> pageClazz, PageParameters pageParameters);

    /**
     * Resolves or finds a mount point configuration by URL
     * @param url URL to find
     * @return matching mount point
     */
    public MountPoint resolveMountPoint(String url);

    /**
     * Saves a list of mount points and sets related content id to it.
     * Deletes existing bindings if a URL is already used.
     *
     * @param mountPoint
     */
    void save(MountPoint mountPoint);

    /**
     * Saves a list of mount points and sets related content id to it.
     * Sets a default URL if no one is selected.
     * Removes empty mount URLs from list (deletes it).
     * Deletes existing bindings if a URL is already used.
     *
     * @param mountPoints mount points to save
     * @param relatedContentId related content id
     */
    void save(List<MountPoint> mountPoints, String relatedContentId);

    /**
     * Deletes the mount point and sets a new default URL
     *
     * @param mountPoint mount point to delete
     */
    void delete(MountPoint mountPoint);

    /**
     * Deletes the mount points and sets a new default URL
     *
     * @param mountPoints list of mount points to delete
     */
    void delete(List<MountPoint> mountPoints);

    /**
     * Deletes the mount point and sets a new default URL
     *
     * @param relatedContentId  releated content id e.g. "3"
     * @param handlerKey handler key e.g. "article"
     */
    void delete(String relatedContentId, String handlerKey);

    /**
     * Finds a list with mount points matching the related content id and handler key
     * @param relatedContentId  releated content id e.g. "3"
     * @param handlerKey handler key e.g. "article"
     * @return list with matching mount moints
     */
    List<MountPoint> findMountPoints(String relatedContentId, String handlerKey);

    /**
     * Returns a list with URLs starting with the urlPrefix, required for auto completion
     *
     * @param urlPrefix url prefix starting with /
     * @return list of matching URLs
     */
    List<String> findMountPointsStartingWith(String urlPrefix);

    /**
     * Returns true if the related content id and handlerKey exists
     *
     * @param relatedContentId  releated content id e.g. "3"
     * @param handlerKey handler key e.g. "article"
     * @return true if exists
     */
    boolean existsMountPoint(String relatedContentId, String handlerKey);
}
