package org.devproof.portal.core.module.mount.service;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.core.module.mount.registry.MountHandlerRegistry;
import org.devproof.portal.core.module.mount.repository.MountPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Service("mountService")
// TODO unit test
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
    public void save(MountPoint mountPoint) {
        mountPointRepository.save(mountPoint);
    }

    @Override
    @Transactional
    public void delete(MountPoint mountPoint) {
        mountPointRepository.delete(mountPoint);
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
        return mountPointRepository.findMountsPointByHandlerKeyAndRelatedContentId(relatedContentId, handlerKey);
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
