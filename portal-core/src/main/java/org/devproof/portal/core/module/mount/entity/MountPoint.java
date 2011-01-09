package org.devproof.portal.core.module.mount.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

/**
 * @author Carsten Hufe
 */
//@Entity
//@Table(name = "mount_point")
public class MountPoint implements Serializable {
    private static final long serialVersionUID = -4190803563987971202L;

    // mount url lowercase
    private Integer id;
//    @Column(unique = true)
    private String mountPath;
    private String relatedContentId;
    private String handlerKey;
    private Integer order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMountPath() {
        return mountPath;
    }

    public void setMountPath(String mountPath) {
        this.mountPath = mountPath;
    }

    public String getRelatedContentId() {
        return relatedContentId;
    }

    public void setRelatedContentId(String relatedContentId) {
        this.relatedContentId = relatedContentId;
    }

    public String getHandlerKey() {
        return handlerKey;
    }

    public void setHandlerKey(String handlerKey) {
        this.handlerKey = handlerKey;
    }

    @Override
    public String toString() {
        return "MountPoint{" + "id=" + id + ", mountPath='" + mountPath + '\'' + ", relatedContentId='" + relatedContentId + '\'' + ", handlerKey='" + handlerKey + '\'' + ", order=" + order + '}';
    }
}
