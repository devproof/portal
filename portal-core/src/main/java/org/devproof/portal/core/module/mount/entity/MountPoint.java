package org.devproof.portal.core.module.mount.entity;

import org.devproof.portal.core.module.common.CommonConstants;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "core_mount_point")
@CacheQuery(region = CommonConstants.QUERY_CORE_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = CommonConstants.ENTITY_CORE_CACHE_REGION)
public class MountPoint implements Serializable {
    private static final long serialVersionUID = -4190803563987971202L;

    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "mount_path", nullable = false, unique = true)
    private String mountPath;
    @Column(name = "related_content_id")
    private String relatedContentId;
    @Column(name = "handler_key", nullable = false)
    private String handlerKey;
    @Column(name = "sort", nullable = false)
    private Integer sort;

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

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "MountPoint{" + "id=" + id + ", mountPath='" + mountPath + '\'' + ", relatedContentId='" + relatedContentId + '\'' + ", handlerKey='" + handlerKey + '\'' + ", sort=" + sort + '}';
    }
}
