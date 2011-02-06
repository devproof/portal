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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    @Column(name = "mount_path", nullable = false, unique = true)
    private String mountPath;
    @Column(name = "related_content_id")
    private String relatedContentId;
    @Column(name = "handler_key", nullable = false)
    private String handlerKey;
    @Column(name = "default_url", nullable = false)
    private boolean defaultUrl = false;

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

    public boolean isDefaultUrl() {
        return defaultUrl;
    }

    public void setDefaultUrl(boolean defaultUrl) {
        this.defaultUrl = defaultUrl;
    }

    public boolean isTransient() {
        return id == null;
    }

    @Override
    public String toString() {
        return "MountPoint{" + "id=" + id + ", mountPath='" + mountPath + '\'' + ", relatedContentId='" + relatedContentId + '\'' + ", handlerKey='" + handlerKey + '\'' + ", defaultUrl=" + defaultUrl + '}';
    }
}
