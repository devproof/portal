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
package org.devproof.portal.module.otherpage.entity;

import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.common.entity.Modification;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.module.otherpage.OtherPageConstants;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "other_page")
@CacheQuery(region = OtherPageConstants.QUERY_CACHE_REGION)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = OtherPageConstants.ENTITY_CACHE_REGION)
@RegisterGenericDataProvider(value = "otherPageDataProvider", sortProperty = "contentId", sortAscending = true)
public class OtherPage extends BaseOtherPage {
    private static final long serialVersionUID = 1L;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "other_page_right_xref", joinColumns = @JoinColumn(name = "other_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "right_id", referencedColumnName = "right_id"))
    private List<Right> allRights;

    public List<Right> getAllRights() {
        if (allRights == null) {
            allRights = new ArrayList<Right>();
        }
        return allRights;
    }

    public void setAllRights(List<Right> allRights) {
        this.allRights = allRights;
    }

    @Transient
    public List<Right> getViewRights() {
        return getRightsStartingWith(allRights, "otherPage.view");
    }
}
