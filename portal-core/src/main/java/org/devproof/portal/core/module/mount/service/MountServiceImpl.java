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

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.core.module.mount.registry.MountHandlerRegistry;
import org.devproof.portal.core.module.mount.repository.MountPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Carsten Hufe
 */
@Service("mountService")
public class MountServiceImpl implements MountService {
    private MountHandlerRegistry mountHandlerRegistry;
    private MountPointRepository mountPointRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> findMountPointsStartingWith(String urlPrefix) {
        return mountPointRepository.findMountPointsStartingWith(urlPrefix);
    }

    @Override
    @Transactional
    public void delete(String relatedContentId, String handlerKey) {
        List<MountPoint> mountPoints = mountPointRepository.findMountPoints(relatedContentId, handlerKey);
        delete(mountPoints);
    }

    @Override
    @Transactional
    public void save(MountPoint mountPoint) {
        boolean exists = false;
        if(mountPoint.isTransient()) {
            exists =  mountPointRepository.existsMountPoint(mountPoint.getMountPath()) > 0;
        }
        else {
            exists =  mountPointRepository.existsMountPoint(mountPoint.getMountPath(), mountPoint.getRelatedContentId(), mountPoint.getHandlerKey()) > 0;
        }
        if(exists) {
            MountPoint existing = mountPointRepository.findMountPointByUrl(mountPoint.getMountPath());
            delete(existing);
        }
        mountPointRepository.save(mountPoint);
    }

    @Override
    @Transactional
    public void save(List<MountPoint> mountPoints, String relatedContentId) {
        List<MountPoint> filteredMountPoints = removeDoubles(mountPoints);
        boolean defaultUrlNotSelected = hasNoDefaultUrl(filteredMountPoints);
        for(MountPoint mountPoint : filteredMountPoints) {
            mountPoint.setRelatedContentId(relatedContentId);
            if(StringUtils.isNotBlank(mountPoint.getMountPath())) {
                if(defaultUrlNotSelected) {
                    mountPoint.setDefaultUrl(true);
                    defaultUrlNotSelected = false;
                }
                save(mountPoint);
            }
            else if(!mountPoint.isTransient()) {
                mountPoint.setMountPath("dummy");
                mountPointRepository.delete(mountPoint);
            }
        }
    }

    private List<MountPoint> removeDoubles(List<MountPoint> mountPoints) {
        Set<String> urls = new HashSet<String>();
        List<MountPoint> result = new ArrayList<MountPoint>();
        for(MountPoint mountPoint : mountPoints) {
            String mountPath = mountPoint.getMountPath();
            if(!urls.contains(mountPath)) {
                result.add(mountPoint);
            }
            urls.add(mountPath);
        }
        return result;
    }

    @Override
    @Transactional
    public void delete(List<MountPoint> mountPoints) {
        for(MountPoint mountPoint : mountPoints) {
            if(!mountPoint.isTransient()) {
                delete(mountPoint);
            }
        }
    }

    @Override
    @Transactional
    public void delete(MountPoint mountPoint) {
        mountPoint.setMountPath("dummy");
        mountPointRepository.delete(mountPoint);
        List<MountPoint> mps = mountPointRepository.findMountPoints(mountPoint.getRelatedContentId(), mountPoint.getHandlerKey());
        if(mps.size() > 0 && hasNoDefaultUrl(mps)) {
            MountPoint mp = mps.get(0);
            mp.setDefaultUrl(true);
            mountPointRepository.save(mp);
        }
    }

    private boolean hasNoDefaultUrl(List<MountPoint> mps) {
        for (MountPoint mp : mps) {
            if(mp.isDefaultUrl() && StringUtils.isNotBlank(mp.getMountPath())) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsMountPoint(String relatedContentId, String handlerKey) {
        return mountPointRepository.existsMountPoint(relatedContentId, handlerKey) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public IRequestTarget resolveRequestTarget(String requestedUrl) {
        requestedUrl = addLeadingSlash(requestedUrl);
        requestedUrl = removeEndingSlash(requestedUrl);
        MountPoint mountPoint = resolveMountPoint(requestedUrl);
        String handlerKey = mountPoint.getHandlerKey();
        if(mountHandlerRegistry.isMountHandlerAvailable(handlerKey)) {
            MountHandler mountHandler = mountHandlerRegistry.getMountHandler(handlerKey);
            return mountHandler.getRequestTarget(requestedUrl, mountPoint);
        }
        throw new IllegalStateException("No mount handler available, should not occur because existing url is prechecked.");
    }

    @Override
    @Transactional(readOnly = true)
    public MountPoint findDefaultMountPoint(String relatedContentId, String handlerKey) {
        return mountPointRepository.findDefaultMountPoint(relatedContentId, handlerKey);
    }

    @Override
    @Transactional(readOnly = true)
    public MountPoint resolveMountPoint(String url) {
        if(StringUtils.isEmpty(url)) {
            return null;
        }
        MountPoint mountPoint = mountPointRepository.findMountPointByUrl(url);
        if(mountPoint == null) {
            String shortenUrl = StringUtils.substringBeforeLast(url, "/");
            return resolveMountPoint(shortenUrl);
        }
        return mountPoint;
    }

    private String addLeadingSlash(String requestedUrl) {
        if(!requestedUrl.startsWith("/")) {
            requestedUrl = "/" + requestedUrl;
        }
        return requestedUrl;
    }

    private String removeEndingSlash(String requestedUrl) {
        if(requestedUrl.endsWith("/")) {
            requestedUrl = StringUtils.removeEnd(requestedUrl, "/");
        }
        return requestedUrl;
    }

    @Override
    @Transactional(readOnly = true)
    public String urlFor(Class<? extends Page> pageClazz, PageParameters params) {
        Collection<MountHandler> mountHandlers = mountHandlerRegistry.getRegisteredMountHandlers().values();
        for(MountHandler handler : mountHandlers) {
            if(handler.canHandlePageClass(pageClazz, params)) {
                return handler.urlFor(pageClazz, params);
            }
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canHandlePageClass(Class<? extends Page> pageClazz, PageParameters pageParameters) {
        Collection<MountHandler> mountHandlers = mountHandlerRegistry.getRegisteredMountHandlers().values();
        for(MountHandler handler : mountHandlers) {
            if(handler.canHandlePageClass(pageClazz, pageParameters)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsPath(String requestedUrl) {
        if(requestedUrl == null) {
            return false;
        }
        requestedUrl = addLeadingSlash(requestedUrl);
        requestedUrl = removeEndingSlash(requestedUrl);
        MountPoint mountPoint = resolveMountPoint(requestedUrl);
        return mountPoint != null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MountPoint> findMountPoints(String relatedContentId, String handlerKey) {
        return mountPointRepository.findMountPoints(relatedContentId, handlerKey);
    }

    @Autowired
    public void setMountPointRepository(MountPointRepository mountPointRepository) {
        this.mountPointRepository = mountPointRepository;
    }

    @Autowired
    public void setMountHandlerRegistry(MountHandlerRegistry mountHandlerRegistry) {
        this.mountHandlerRegistry = mountHandlerRegistry;
    }
}
