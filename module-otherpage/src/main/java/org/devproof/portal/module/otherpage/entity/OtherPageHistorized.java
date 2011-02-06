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
import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.historization.service.Historized;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.module.otherpage.OtherPageConstants;
import org.devproof.portal.module.otherpage.query.OtherPageHistoryQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "other_page_historized")
@RegisterGenericDataProvider(value = "otherPageHistoryDataProvider", sortProperty = "versionNumber", sortAscending = false, queryClass = OtherPageHistoryQuery.class)
public class OtherPageHistorized extends BaseOtherPage implements Historized {
    private static final long serialVersionUID = 1L;

    @Column(name = "version_number")
    private Integer versionNumber;
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private Action action;
    @Column(name = "action_at")
    private Date actionAt;
    @Column(name = "restored_from_version")
    private Integer restoredFromVersion;
    @Lob
    @Column(name = "rights")
    private String rights;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "other_page_id")
    private OtherPage otherPage;

    public OtherPage getConvertedOtherPage() {
        OtherPage otherPage = new OtherPage();
        otherPage.copyFrom(this);
        return otherPage;
    }

    @Override
    public Action getAction() {
        return action;
    }

    @Override
    public Date getActionAt() {
        return actionAt;
    }

    @Override
    public Integer getVersionNumber() {
        return versionNumber;
    }

    @Override
    public Integer getRestoredFromVersion() {
        return restoredFromVersion;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setActionAt(Date actionAt) {
        this.actionAt = actionAt;
    }

    public void setRestoredFromVersion(Integer restoredFromVersion) {
        this.restoredFromVersion = restoredFromVersion;
    }

    public String getRights() {
        return rights;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public OtherPage getOtherPage() {
        return otherPage;
    }

    public void setOtherPage(OtherPage otherPage) {
        this.otherPage = otherPage;
    }
}
