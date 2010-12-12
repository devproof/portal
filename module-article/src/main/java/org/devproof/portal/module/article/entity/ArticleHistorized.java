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
package org.devproof.portal.module.article.entity;

import org.apache.commons.lang.StringUtils;
import org.devproof.portal.core.config.RegisterGenericDataProvider;
import org.devproof.portal.core.module.common.annotation.CacheQuery;
import org.devproof.portal.core.module.historization.service.Action;
import org.devproof.portal.core.module.historization.service.Historized;
import org.devproof.portal.core.module.right.entity.Right;
import org.devproof.portal.module.article.ArticleConstants;
import org.devproof.portal.module.article.query.ArticleHistoryQuery;
import org.devproof.portal.module.article.query.ArticleQuery;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Carsten Hufe
 */
@Entity
@Table(name = "article_historized")
@RegisterGenericDataProvider(value = "articleHistoryDataProvider", sortProperty = "versionNumber", sortAscending = false, queryClass = ArticleHistoryQuery.class)
public class ArticleHistorized extends BaseArticle implements Historized {
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
    @Lob
    @Column(name = "tags")
    private String tags;
    @Lob
    @Column(name = "full_article")
    private String fullArticle;
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    private Article article;

    public void copyFrom(Article modification) {
        super.copyFrom(modification);
        setFullArticle(modification.getFullArticle());
    }

    public String getFullArticle() {
        return fullArticle;
    }

    public void setFullArticle(String fullArticle) {
        this.fullArticle = fullArticle;
    }

    @Override
    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public Date getActionAt() {
        return actionAt;
    }

    public void setActionAt(Date actionAt) {
        this.actionAt = actionAt;
    }

    @Override
    public Integer getRestoredFromVersion() {
        return restoredFromVersion;
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

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }
}
