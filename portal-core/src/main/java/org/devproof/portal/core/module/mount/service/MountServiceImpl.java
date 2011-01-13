package org.devproof.portal.core.module.mount.service;

import org.apache.wicket.IRequestTarget;
import org.devproof.portal.core.module.mount.entity.MountPoint;
import org.devproof.portal.core.module.mount.registry.MountHandler;
import org.devproof.portal.core.module.mount.registry.MountHandlerRegistry;
import org.devproof.portal.core.module.mount.repository.MountPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public IRequestTarget resolveRequestTarget(String requestedUrl) {
        MountPoint mountPoint = mountPointRepository.findMountPointByUrl("/" + requestedUrl);
        // TODO wenn keiner gefunden ? default delegieren?
        String handlerKey = mountPoint.getHandlerKey();
        if(mountHandlerRegistry.isMountHandlerAvailable(handlerKey)) {
            MountHandler mountHandler = mountHandlerRegistry.getMountHandler(handlerKey);
            return mountHandler.getRequestTarget(requestedUrl, mountPoint);
        }
        return null;
    }

    @Override
    public void moveUp(MountPoint mountPoint) {
    }

    @Override
    public void moveDown(MountPoint mountPoint) {
    }

    @Override
    public void addMountPoint(String path, String relatedContentId, String handlerKey) {
    }

    @Override
    public void removeMountPoint(String path) {
    }

    @Override
    public List<MountPoint> findMountPoints(String relatedContentId, String handlerKey) {
        return null;
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
