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
//    List<String> findSimilarUrls(String url);
    @Query("select mp from MountPoint mp where mp.mountPath like ?")
    MountPoint findMountPointByUrl(String url);

    @Query("select mp from MountPoint mp where mp.relatedContentId like ? and mp.handlerKey like ? order by mp.sort")
    List<MountPoint> findMountPointByHandlerKeyAndRelatedContentId(String relatedContentId, String handlerKey);

    @Query("select count(mp) from MountPoint mp where mp.mountPath like ?")
    long existsMountPointUrl(String url);

    @Query("select count(mp) from MountPoint mp where mp.relatedContentId like ? and mp.handlerKey like ?")
    long existsMountPoint(String relatedContentId, String handlerKey);
}
