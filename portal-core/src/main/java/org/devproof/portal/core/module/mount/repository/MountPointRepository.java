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
package org.devproof.portal.core.module.mount.repository;

import org.devproof.portal.core.config.GenericRepository;
import org.devproof.portal.core.module.common.annotation.Query;
import org.devproof.portal.core.module.common.repository.CrudRepository;
import org.devproof.portal.core.module.mount.entity.MountPoint;

import java.util.List;

/**
 * MountPoint repository
 *
 * @author Carsten Hufe
 */
@GenericRepository("mountPointRepository")
public interface MountPointRepository extends CrudRepository<MountPoint, Integer> {
    @Query("select mp from MountPoint mp where mp.mountPath like ?")
    MountPoint findMountPointByUrl(String url);

    @Query("select count(mp) from MountPoint mp where mp.relatedContentId not like ? and mp.handlerKey not like ? and mp.defaultUrl = true")
    long hasDefaultUrl(String relatedContentId, String handlerKey);

    @Query("select count(mp) from MountPoint mp where mp.mountPath like ? and (mp.relatedContentId not like ? or mp.handlerKey not like ?)")
    long existsMountPoint(String url, String relatedContentId, String handlerKey);

    @Query("select count(mp) from MountPoint mp where mp.mountPath like ?")
    long existsMountPoint(String url);

    @Query("select mp from MountPoint mp where mp.relatedContentId like ? and mp.handlerKey like ? and mp.defaultUrl = 1")
    MountPoint findDefaultMountPoint(String relatedContentId, String handlerKey);

    @Query("select mp from MountPoint mp where mp.relatedContentId like ? and mp.handlerKey like ? order by mp.defaultUrl desc")
    List<MountPoint> findMountPoints(String relatedContentId, String handlerKey);

    @Query("select mp.mountPath from MountPoint mp where mp.mountPath like ?||'%' order by mp.mountPath")
    List<String> findMountPointsStartingWith(String mountPathStart);

    @Query("select count(mp) from MountPoint mp where mp.relatedContentId like ? and mp.handlerKey like ?")
    long existsMountPoint(String relatedContentId, String handlerKey);
}
